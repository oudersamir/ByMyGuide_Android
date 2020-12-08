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


      $sql = 'insert into locations(lat,lng,zoom,place) values(:lat,:lng,:zoom,:place)';
 
    $q = $conn->prepare($sql);
    
     

    
 


/*$sql2 = 'SELECT * FROM locations';
$q2 = $conn->prepare($sql2);
$q2->execute([]);



$locations=$q2->fetchAll();

$jsondata = json_encode($locations);
 echo $jsondata ;*/

if($q->execute([':lat'=>$_POST['lat'],':lng'=>$_POST['lng'],':zoom'=>$_POST['zoom'],':place'=>$_POST['place']])){
        //making success response 
        $response['error'] = false; 
        $response['message'] = 'Name saved successfully'; 
    }else{
        //if not making failure response 
        $response['error'] = true; 
        $response['message'] = 'Please try later';
    }
    

   

}

    
catch(PDOException $e)
    {
    echo "Connection failed: " . $e->getMessage();
    }


  


}
else{
    $response['error'] = true; 
    $response['message'] = "Invalid request"; 
}

   echo json_encode($response);

?>