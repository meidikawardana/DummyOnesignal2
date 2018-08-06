<?php 
class Users_model extends CI_Model {

	public function __construct(){		
	}	
	
	function is_exists($name,$username){
		$sql = "
			SELECT id 
				FROM users 
			WHERE name = ? or username = ?
		";
		
		$query = $this->db->query($sql,array(
				$name, $username				
			));
		
		return ($query->num_rows() > 0) ? 1:0;
	}
	
	function is_other_user_exists($name,$username, $user_id){
	    $sql = "
			SELECT id
				FROM users
			WHERE (name = ? or username = ?) and id != ?
		";
	    
	    $query = $this->db->query($sql,array(
	        $name, $username, $user_id
	    ));
	    
	    return ($query->num_rows() > 0) ? 1:0;
	}
	
	function insert($data){
		$sql = "
			INSERT INTO users
				(username, name, pass)
				VALUES (?, ?, ?)
		";
		
		$query = $this->db->query($sql,array(
				  $data->username
				, $data->name
				, $data->pass
			));
		
		if($query)
			return $this->db->insert_id();
		return 0;		
	}
	
	function load(){
		$sql = "
					SELECT id, username, name
					FROM users
					order by name
				";
		$query = $this->db->query($sql);
		
		return $query->result();
	}
	
	function update($params){
		$sql = "
			UPDATE users
				SET					
					username=?,
					name=?
				WHERE id=?
		";
		
		$query = $this->db->query($sql,array(
				$params->username
				, $params->name
				, $params->user_id
			));
		
		return $query;
	}
	
	function update_onesignal_userid($params){
		$sql = "
			UPDATE users
				SET										
					onesignal_userid=?
				WHERE id=?
		";
		
		$query = $this->db->query($sql,array(
				$params->onesignal_userid
				, $params->user_id
			));
		
		return $query;
	}	
	
	function delete($params){
	    $sql = "
			DELETE FROM users WHERE id=?
		";
	    
	    $query = $this->db->query($sql,array(
	        $params->user_id
	    ));
	    
	    return $query;
	}
	
	function is_username_pass_exists($username,$pass){
		$sql = "
			SELECT id 
				FROM users 
			WHERE username = ? and pass = ?
		";
		
		$query = $this->db->query($sql,array(
				$username, $pass				
			));
		
		return ($query->num_rows() > 0) ? 1:0;
	}
	
	function load_one_user($username, $pass){
		$sql = "
					SELECT id, username, name
					FROM users where username = ? and pass = ?
				";
		$query = $this->db->query($sql, array($username, $pass));
		
		return $query->row();
	}

	function load_one_user_by_id($user_id){
		$sql = "
					SELECT id, username, name, onesignal_userid
					FROM users where id = ?
				";
		$query = $this->db->query($sql, array($user_id));
		
		return $query->row();
	}
	
}