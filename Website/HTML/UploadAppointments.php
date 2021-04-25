<script src="../node_modules/axios/dist/axios.min.js"></script>
<script src="../Scripts/Network.js"></script>

<script>

    function SendPath(path)
    {
        axios.post("http://" + GetIP() + ":7000/uploadApp", {
            pathname: path
        },
        {
            headers: {
                "Access-Control-Allow-Origin": "*",
                "Access-Control-Allow-Methods": "GET, POST, PATCH, PUT, DELETE, OPTIONS"
            }
        }
        ).then((response) => {
            console.log(response);
        }, (error) => {
            console.log(error);
            console.log("you got an error");
        });
    }
</script>

<?php
//save uploaded appointments in directory called uploads under HTML dir
$target_dir = "../../Data Access Server/java/";
$target_file = $target_dir . basename($_FILES["fileToUpload"]["name"]);
$uploadOk = 1;
$file_type = strtolower(pathinfo($target_file,PATHINFO_EXTENSION));

// Check if file already exists
if (file_exists($target_file)) {
    echo "This file already exists.";
    $uploadOk = 0;
}


// Allow csv file formats
if ($file_type != "csv") {
    echo "Sorry, only CSV files are allowed.";
    $uploadOk = 0;
}

// Check if $uploadOk is set to 0 by an error
if ($uploadOk == 0) {
    echo "Your file was not uploaded.";
    // if everything is ok, try to upload file
} else {
    if (move_uploaded_file($_FILES["fileToUpload"]["tmp_name"], $target_file)) {
        echo "The file " . htmlspecialchars(basename($_FILES["fileToUpload"]["name"])) . " has been uploaded.";
        echo "<script>SendPath('" . basename($_FILES["fileToUpload"]["name"]) . "')</script>";

    } else {
        echo "ERROR occurred while uploading the file.";
    }

    // link to the previous page

    echo '<a href="import_appointments.php"> GO BACK </a>';
}
?>