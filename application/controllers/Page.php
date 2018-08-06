<?php
defined('BASEPATH') OR exit('No direct script access allowed');

//class ini untuk menampilkan halaman2 ke user di website
class Page extends CI_Controller {
	
	public function index(){
		$this->load->model('users_model'); //muat file users_model di folder models
		
		$data = array();
		$data['users'] = $this->users_model->load(); //memuat semua data user ke variabel $data['users']
	
		$this->load->view('main_view', $data); //tampilkan file main_view.php di folder view		
	}	
}