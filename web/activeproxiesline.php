<?php
$db = mysqli_connect("localhost", "root2", "", "proxies");
$start = 0;
$limit = 100;
if (isset($_GET['start'])) {
    $start = mysqli_real_escape_string($db, $_GET['start']);
}
if (isset($_GET['limit'])) {
    $limit = mysqli_real_escape_string($db, $_GET['limit']);
}
$type = false;
if (isset($_GET['type'])) {
    $type = mysqli_real_escape_string($db, $_GET['type']);
}
if (!$type) {
    $res = mysqli_query($db, "SELECT * FROM proxymity_proxies WHERE status='active' LIMIT $start, $limit");
} else {
//'socks4','socks5','http','https'
    $res = mysqli_query($db, "SELECT * FROM proxymity_proxies WHERE status='active' AND `type`='$type' LIMIT $start, $limit");
}
echo mysqli_error($db);
$results = array();
while ($row = mysqli_fetch_array($res)) {
    print $row['host'] . ":" . $row['port'] . "\n";
    $results[] = $row;
}
die();
?>
