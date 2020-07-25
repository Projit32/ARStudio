<?php
$dir="objects/";
$FileList=scandir($dir);
$listArr=[];
for ($i=2;$i<count($FileList) ;$i++) {
	$listArr[]=array("name"=>$FileList[$i]);
}

$listArr=json_encode($listArr,JSON_PRETTY_PRINT);
echo $listArr;

?>