# Proxymity

## What this repo does

Proxymity is a Java-based proxy harvesting and validation system.

At a high level it:

- Scrapes many public proxy sources in parallel
- Normalizes discovered proxies into `host:port:type`
- Stores them in MySQL as `pending`
- Continuously re-checks them for reachability, anonymity, and HTTPS support
- Exposes stored proxies through a minimal PHP read API

In plain terms: it is a proxy collection and verification pipeline backed by MySQL.

## Runtime flow

Main entrypoint:

- `src/main/java/com/object0r/tools/proxymity/Main.java`

Startup behavior:

- Loads `config.properties`
- Configures logging
- Prevents duplicate runs with `RecurringProcessHelper`
- Instantiates `Proxymity`
- Starts checker threads
- Starts collector threads

Core bootstrap:

- `src/main/java/com/object0r/tools/proxymity/Proxymity.java`

`Proxymity` is responsible for:

- Reading runtime configuration
- Connecting to MySQL
- Creating the database and table schema if missing
- Starting status reporting
- Launching collectors and checkers

Operational model:

- This is intended to run as a long-lived service
- It compiles and maintains a large inventory of proxies over time
- It retries and revalidates proxies continuously, but on an interval-driven schedule rather than by hammering endpoints

## Main subsystems

### 1. Collectors

Main manager:

- `src/main/java/com/object0r/tools/proxymity/ProxyCollectorManager.java`

Behavior:

- Builds a large list of source-specific collector threads
- Starts all collectors in parallel
- Periodically writes collected proxies into MySQL
- Tracks per-collector counts

Collector base class:

- `src/main/java/com/object0r/tools/proxymity/ProxyCollector.java`

Collector loop:

1. Sleep briefly
2. Clear in-memory proxy list
3. Scrape a source
4. Insert results into MySQL as `pending`
5. Sleep and repeat

Collector cadence:

- Collectors are continuous loops with a sleep between scans
- This is designed for steady long-term discovery, not burst collection

Collectors are site-specific scrapers. Some parse plain text or HTML directly. Others use PhantomJS/Selenium to render JavaScript-heavy pages first.

Representative collectors:

- `src/main/java/com/object0r/tools/proxymity/collectors/GithubCollector.java`
- `src/main/java/com/object0r/tools/proxymity/collectors/HmaCollector.java`

Notes:

- `GithubCollector` aggregates many GitHub-hosted proxy lists
- `HmaCollector` demonstrates the browser-assisted scraping path

### 2. PhantomJS worker pool

Relevant files:

- `src/main/java/com/object0r/tools/proxymity/phantomjs/PhantomJsManager.java`
- `src/main/java/com/object0r/tools/proxymity/phantomjs/PhantomJsWorker.java`

Behavior:

- Maintains a queue of browser jobs
- Starts multiple PhantomJS workers
- Loads pages with JavaScript enabled
- Supports cookies and synthetic POST submissions
- Returns page text and source back to collectors

This exists to scrape sources that require browser-like execution rather than plain HTTP fetches.

### 3. Checkers

Main manager:

- `src/main/java/com/object0r/tools/proxymity/ProxyCheckerManager.java`

Behavior:

- Starts a large pool of checker threads
- Prioritizes `pending` proxies
- Re-checks non-dead proxies on a configurable interval
- Re-checks previously dead proxies later
- Marks stale inactive proxies as `dead`
- Replaces checker threads it considers inactive

Recheck policy:

- Verified or otherwise non-dead proxies are revisited more often
- Dead proxies are still retried, but less often
- The intended behavior is to keep a large proxy pool warm over time without bombarding the same targets

Checker worker:

- `src/main/java/com/object0r/tools/proxymity/ProxyChecker.java`

Per-proxy behavior:

1. Open the proxy port
2. Fetch external IP through the proxy
3. Reject proxies that expose the host machine IP
4. Mark working proxies as `active`
5. Test HTTPS support against the configured verification target
6. Update MySQL fields such as `status`, `remoteIp`, `https`, `lastchecked`, and `lastactive`

## Data model

Schema file:

- `dbSchema.sql`

Primary table:

- `proxymity_proxies`

Important fields:

- `host`
- `port`
- `type`
- `status`
- `fullanonymous`
- `remoteIp`
- `lastchecked`
- `lastactive`
- `https`
- `checkOnlyOnce`
- `priority`
- `source`

Important caveat:

- The code performs a practical IP leak check, but the `fullanonymous` field is not reliably maintained by the active checker flow
- This functionality is required and must be preserved in any rewrite
- The rewrite should complete the feature by making `fullanonymous` detection and marking explicit, correct, and persisted

Important statuses:

- `pending`
- `active`
- `inactive`
- `dead`

Status lifecycle:

- Newly discovered proxies enter as `pending`
- Working proxies become `active`
- Failed proxies typically become `inactive`
- Proxies that remain inactive long enough are marked `dead`
- Dead proxies are not discarded permanently; they are retried on a slower path

## IP resolvers

Proxymity resolves public IPs through the sibling `toortools` library.

Resolver call sites in this repo:

- `src/main/java/com/object0r/tools/proxymity/ProxyChecker.java`
- `../toortools/src/main/java/com/object0r/toortools/Utilities.java`
- `../toortools/src/main/java/com/object0r/toortools/providers/ip/IpProvidersHelper.java`

How they are used:

- `Utilities.getIp()` resolves the host machine public IP
- `Utilities.getIp(proxy, ...)` resolves the public IP exposed through a candidate proxy
- The checker compares those values to reject non-anonymous proxies

Leak detection behavior:

- The active anonymity check lives in `src/main/java/com/object0r/tools/proxymity/ProxyChecker.java`
- The checker stores the local machine public IP in `myIp`
- For each candidate proxy it resolves the public IP seen through that proxy
- If the proxied IP equals `myIp`, the proxy is rejected as no good
- If the proxied IP differs, the proxy is accepted and the observed remote IP is stored in `remoteIp`
- In project terms, a `fullanonymous` proxy is one that does not leak the originator's IP at all

Required behavior for the Go rewrite:

- Preserve the end-user IP leak check as a first-class feature
- Make `fullanonymous` mean exactly this: the proxy must not reveal the originator's public IP
- Do not treat `fullanonymous` as cosmetic metadata; it is core proxy-quality behavior
- Implement full detection and explicit marking of `fullanonymous` proxies
- Persist the result explicitly in the data model instead of leaving it implied by transient checker logic
- Keep the rewrite Go-only at runtime; do not split behavior across PHP or other side services

Important mismatch in current implementation:

- There is a `fullanonymous` column in the schema
- There is also a `markProxyAnonymous(...)` method in `ProxyChecker`
- But that method is not called from the active checking path
- Result: the system does enforce a basic "does this proxy leak our IP" check, but it does not persist that result into `fullanonymous`
- For the planned Go rewrite, this is not optional cleanup; the missing part is exactly what should be finished: proper `fullanonymous` detection and reliable marking

Active resolver endpoints in the provider pool:

- `http://icanhazip.com/`
- `http://api.ipify.org/?format=txt`
- `https://api.duckduckgo.com/?q=my+ip&format=json`
- `http://ipof.in/txt`
- `http://ipv4bot.whatismyipaddress.com/`
- `http://myexternalip.com/raw`
- `http://myip.dnsomatic.com/`
- `http://whatismyip.org/`
- `http://ipecho.net/plain`
- `http://checkip.amazonaws.com/`

Resolver implementations that exist but are not currently enabled in the pool:

- `http://cpanel.com/showip.shtml`
- `http://ip-api.com/line/?fields=query`
- `http://checkip.dyndns.org/`

Separate from the public IP resolvers, HTTPS capability is verified against:

- `httpbin.org/ip`

That value is configurable in:

- `config.properties.sample`
- `src/main/java/com/object0r/tools/proxymity/Proxymity.java`

## Web/API surface

PHP endpoints:

- `web/activeproxies.php`
- `web/proxies.php`
- `web/activeproxiesline.php`

Behavior:

- Read directly from MySQL
- Return active or all proxies
- Provide either JSON output or line-based `host:port` output

These are thin read-only access layers over the database, not a full application backend.

Rewrite direction:

- Remove the PHP layer in the rewrite
- Any HTTP/API surface should be implemented directly in Go if still needed
- Do not preserve the current PHP files as an architectural requirement

## Dependencies and environment

Declared in:

- `pom.xml`
- `README.md`
- `config.properties.sample`

Important runtime dependencies:

- Java 8
- MySQL
- PhantomJS
- ImageMagick
- GOCR

Optional behavior:

- Tor can be enabled through configuration

Rewrite platform direction:

- Use SQLite instead of MySQL
- Use Go as the only runtime language
- Do not assume Docker is needed
- Unless a concrete deployment or packaging requirement appears, prefer a plain native Go service over containerization

## Practical mental model

When working in this repo, think of it as three loops running forever:

- Discover proxies
- Verify proxies
- Serve verified proxies from MySQL

The codebase is functional but dated. It relies on:

- Heavy thread usage
- PhantomJS/Selenium
- Raw SQL string construction
- Many brittle source-specific scrapers

This is best understood as a proxy farm pipeline rather than a polished modern service.

## Good starting points for future work

If you need to understand or modify behavior, start here:

- `src/main/java/com/object0r/tools/proxymity/Main.java`
- `src/main/java/com/object0r/tools/proxymity/Proxymity.java`
- `src/main/java/com/object0r/tools/proxymity/ProxyCollectorManager.java`
- `src/main/java/com/object0r/tools/proxymity/ProxyCollector.java`
- `src/main/java/com/object0r/tools/proxymity/ProxyCheckerManager.java`
- `src/main/java/com/object0r/tools/proxymity/ProxyChecker.java`
- `dbSchema.sql`
- `web/activeproxies.php`
