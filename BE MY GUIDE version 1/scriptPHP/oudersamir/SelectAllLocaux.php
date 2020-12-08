<?php
 

//making an array to store the response
$response = array(); 
 
//if there is a post request move ahead 
if($_SERVER['REQUEST_METHOD']=='POST'){
    
 




$servername = "localhost";
$username = "root";
$password = "";

try {
    $conn = new PDO("mysql:host=$servername;dbname=locationmarkersqlite", $username, $password);
    // set the PDO error mode to exception
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
     

    
 


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