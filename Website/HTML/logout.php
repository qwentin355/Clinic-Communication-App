<script src="../node_modules/axios/dist/axios.min.js"></script>
<script src="../Scripts/Network.js"></script>

<?php session_start(); /* Starts the session */

$user = $_SESSION['UserData']['Username'];
$session = $_COOKIE['sessionID'];
$status = 0;
echo "
            <script>
                console.log('Script');
                let user = '$user';
                let session = '$session';
                let formData = new FormData();
                formData.append('username', user);
                formData.append('sessionID', session);
                axios({
                    method: 'post',
                    url: 'http://' + GetIP() + ':7000/logout',
                    data: formData,
                    headers: { 'Content-Type': 'multipart/form-data' }
                }).then(function (response) {
                    //handle success

                    let rs = response.status;
                     console.log(rs);
                    if(rs == 200){
                        document.cookie = 'status=200';
                        var formdata = new FormData();
                    }
                    else{
                        document.cookie = 'status=401';
                    }
                    console.log(response);
                   location.reload();
                }).catch(function (response) {
                    document.cookie = 'status=401';
                    console.log(response);
                 location.reload();
                });
            </script>
        ";
if(isset($_COOKIE['status'])) {
    $status = $_COOKIE['status'];
    if ($status == '200') {
       setcookie("status", "", time() - 1);
       setcookie("sessionID", "", time() - 1);
       setcookie("uname", "", time() - 1);
        session_destroy(); /* Destroy started session */

        header("location:login.php");
        exit;
    }
}
?>
