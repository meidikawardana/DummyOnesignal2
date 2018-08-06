<?php
defined('BASEPATH') OR exit('No direct script access allowed');

//class ini untuk melayani permintaan dari website & menyediakan data untuk website
class Api extends CI_Controller {
	
	//fungsi ini untuk mengirim notifikasi ke hape menggunakan onesignal
	public function send_notification(){

		//tampung data dari website
		$user_id = $this->input->post('user_id');
		$title = $this->input->post('title');		
		$message = $this->input->post('message');
		
		$this->load->model('users_model');
		$a_user = $this->users_model->load_one_user_by_id($user_id); //mendapatkan data user berdasarkan userid
		
	
		$heading = array(
		   "en" => $title //ini adalah judul di notifikasi
		);		
	
		$content = array(
			"en" => $message //ini adalah pesan di notifikasi
			);
		
		$fields = array(
			'app_id' => "xxxx" //isi ini dengan app id onesignal
			, 'filters' => array(
				array("field" => "tag", "key" => "user_id", "relation" => "=", "value" => strval($user_id))
			)
			// 'include_player_ids' => array(
				// $a_user->onesignal_userid
			// ),
			// 'data' => array("foo" => "bar"),
			, 'contents' => $content //set pesan untuk notifikasi
			, 'headings' => $heading //set judul untuk notifikasi
		);
		
		$fields = json_encode($fields); //simpan data ke variabel $fields
		
		$ch = curl_init();
		curl_setopt($ch, CURLOPT_URL, "https://onesignal.com/api/v1/notifications");
		curl_setopt($ch, CURLOPT_HTTPHEADER, array('Content-Type: application/json; charset=utf-8',
												   'Authorization: Basic *tulis rest api key di sini*'));
		curl_setopt($ch, CURLOPT_RETURNTRANSFER, TRUE);
		curl_setopt($ch, CURLOPT_HEADER, FALSE);
		curl_setopt($ch, CURLOPT_POST, TRUE);
		curl_setopt($ch, CURLOPT_POSTFIELDS, $fields); //set data notifikasi untuk dikirim ke server onesignal
		curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, FALSE);

		$response = curl_exec($ch); //kirim data ke server onesignal
		curl_close($ch);
		
		echo json_encode($response); //tampilkan pesan dari server onesignal
	}
}
