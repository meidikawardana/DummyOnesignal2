<!DOCTYPE html>
<html lang="en">
<head>
  <title>Dummy Onesignal2 Backend</title>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.1.0/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.0/umd/popper.min.js"></script>
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.1.0/js/bootstrap.min.js"></script>
  <style>
	body{
		padding:16px;
	}
  </style>
</head>
<body>

<div class="container">
  <form id="form">
    <div class="form-group">
      <label for="email">Target User</label>
	  <select class="form-control" id="user_id">
		<option value="0"> -- Pilih User -- </option>
		<?php foreach($users as $a_user){ ?>
			<option value="<?php echo $a_user->id; ?>"><?php echo $a_user->name; ?></option>
		<?php } ?>
	  </select>
    </div>
	<div class="form-group">
      <label for="email">Notification title</label>
      <input type="text" class="form-control" id="title" placeholder="Enter notification title" name="title">
    </div>
	<div class="form-group">
      <label for="email">Notification message</label>
      <input type="text" class="form-control" id="message" placeholder="Enter notification message" name="message">
    </div>		
    <button type="button" class="btn btn-primary" id="btnSend">Submit</button>
  </form>	
</div>
	<script>
		$(document).ready(function(){
			$("#btnSend").click(function(){
				
				if($("#user_id").val() == 0){
					alert("Mohon pilih user untuk dikirimi notifikasi");
					return;
				}
				
				//simpan data ke server
				$.post( '<?php echo base_url(); ?>Api/send_notification'
					, {
						user_id: $("#user_id").val()
						, title: $("#title").val()
						, message: $("#message").val()
					}
					, function(data) {
						
						console.log(data);
						
						data_json = JSON.parse(data);
						
						if (typeof data_json.id !== 'undefined' && typeof data_json.errors === 'undefined') {
							alert("sukses mengirim notifikasi");
						}else{
							alert("gagal mengirim notifikasi");
						}
				   },
				   'json'
				);
			});
		});
	</script>
</body>
</html>
