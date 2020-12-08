<?php
if(isset($_GET['place']) &&  isset($_GET['lat']) && isset($_GET['lng']) && isset($_GET['zoom'])){



$servername = "localhost";
$username = "root";
$password = "";

try {
    $conn = new PDO("mysql:host=$servername;dbname=locationmarkersqlite", $username, $password);
    // set the PDO error mode to exception
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);


      $sql = 'insert into locations(lat,lng,zoom,place) values(:lat,:lng,:zoom,:place)';
 
    $q = $conn->prepare($sql);
    $q->execute([':place'=>$_GET['place'],':lat'=>$_GET['lat'],':lng'=>$_GET['lng'],':zoom'=>$_GET['zoom']]);
     

    
 


$sql2 = 'SELECT * FROM locations';
$q2 = $conn->prepare($sql2);
$q2->execute([]);



$locations=$q2->fetchAll();

$jsondata = json_encode($locations);
 echo $jsondata ;



   }

    
catch(PDOException $e)
    {
    echo "Connection failed: " . $e->getMessage();
    }


  




}
?>