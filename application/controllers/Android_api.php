<?php
defined('BASEPATH') OR exit('No direct script access allowed');

//class ini untuk menyediakan data untuk aplikasi android
class Android_api extends CI_Controller {

	public function index(){
		echo "backend app dummy onesignal2";
	}
	
	//fungsi ini untuk mengecek & menyediakan data user ketika login app android
	public function login(){
		//buat variabel untuk menampung data yang akan dikirim ke app android
		$result = new stdClass();	
	
		//tampung data dari app android ke variabel
		$username = $this->input->post('username');
		$pass = $this->input->post('pass');		
		
		if($username === FALSE || $pass === FALSE){ //jika data username atau password tidak ada / tidak valid
			$result->status = 0;
			$result->msg = "Maaf, akses tidak diperbolehkan"; //tampilkan pesan akses dilarang
			echo json_encode($result);
			die();
		}
		
		$oneSignalUserId = $this->input->post('oneSignalUserId'); //mendapatkan userid onesignal (nilainya bisa berupa string kosong)
		
		//muat file users_model.php dari folder models
		$this->load->model('users_model');
	
		
		if(!$this->users_model->is_username_pass_exists($username, $pass)){ //cek apakah username & password sudah ada
			//jika tidak ada, berarti username / password salah
			$result->status = 0;
			$result->msg = "Maaf, username / password salah";
			$result->username = $username;
			$result->pass = $pass;
		}else{
			//jika ada, kirimkan pesan sukses & data user untuk disimpan di aplikasi
			$a_user = $this->users_model->load_one_user($username, $pass);
			
			//buat obyek untuk update userid onesignal di database
			$params = new stdClass();
			$params->onesignal_userid = $oneSignalUserId;
			$params->user_id = $a_user->id;
			
			//update userid onesignal di database
			$this->users_model->update_onesignal_userid($params);
			
			$result->status = 1;
			$result->msg = "Sukses masuk aplikasi";
			$result->a_user = $a_user;
		}
		
		echo json_encode($result);
	}

}
