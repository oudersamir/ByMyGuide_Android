<?php
if(isset($_GET['nom']) &&  isset($_GET['password'])){



$servername = "localhost";
$username = "root";
$password = "";

try {
    $conn = new PDO("mysql:host=$servername;dbname=userjson", $username, $password);
    // set the PDO error mode to exception
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);


      $sql = 'SELECT prenom
               FROM user
              WHERE nom =:nom  and  password=:password';
 
    $q = $conn->prepare($sql);
    $q->execute([':nom'=>$_GET['nom'],':password'=>$_GET['password']]);
    $q->setFetchMode(PDO::FETCH_ASSOC);
    $c=0;
    while ($r = $q->fetch()) {
        $c++;
    }
if($c>0)
   {
$sql2 = 'SELECT * FROM person';
$q2 = $conn->prepare($sql2);
$q2->execute([]);
//var_dump($q2);



$persons=$q2->fetchAll();

$jsondata = json_encode($persons);
 echo $jsondata ;
   }

    }
catch(PDOException $e)
    {
    echo "Connection failed: " . $e->getMessage();
    }


  




}
?>