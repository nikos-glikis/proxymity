<?php
$db = mysqli_connect("localhost", "root", "", "proxies");
$start=0;
$limit=100;
if (isset($_GET['start']))
{
$start = mysqli_add_slashes($_GET['start']);
}
if (isset($_GET['limit']))
{
$limit = mysqli_add_slashes($_GET['limit']);
}
$res = mysqli_query($db, "SELECT * FROM proxymity_proxies");
echo mysqli_error($db);
$results = array();
while ($row = mysqli_fetch_array($res))
{
	$results[] = $row;
}
return json_encode($results);
?>
