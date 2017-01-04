
DROP TABLE tbl_user_login_account;

CREATE TABLE IF NOT EXISTS tbl_user_login_account (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  user_id INTEGER,
  account VARCHAR (2024),
  password varchar(1024)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='users';


/** application key **/
CREATE TABLE IF NOT EXISTS tbl_application_key (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  host varchar(1024) NOT NULL,
  public_key text,
  private_key text,
  passphrase text
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='application key';

/** machine **/
CREATE TABLE IF NOT EXISTS tbl_machine (
	id INTEGER PRIMARY KEY AUTO_INCREMENT,
	display_nm varchar(1024) NOT NULL,
	host varchar(1024) NOT NULL,
	port INTEGER NOT NULL,
	authorized_keys text NOT NULL,
	user varchar(1024) NOT NULL,
	password varchar(1024) NOT NULL,
	status_cd varchar(1024) NOT NULL DEFAULT 'INITIAL'
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='machine';

/** label **/
CREATE TABLE IF NOT EXISTS tbl_label (
	id INTEGER PRIMARY KEY AUTO_INCREMENT,
	nm varchar(1024) NOT NULL,
	`desc` text NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='label';

/** machine user map **/
CREATE TABLE IF NOT EXISTS tbl_machine_user_map (
	id INTEGER PRIMARY KEY AUTO_INCREMENT,
	user varchar(1024) NOT NULL,
	password varchar(1024) NOT NULL,
	machine_id INTEGER,
	FOREIGN KEY (machine_id) REFERENCES tbl_machine(id) ON DELETE CASCADE ,
	status_cd varchar(1024) NOT NULL DEFAULT 'INITIAL'
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='machine user map';

/** machine label map **/
CREATE TABLE IF NOT EXISTS tbl_machine_label_map (
	machine_id INTEGER,
	label_id INTEGER,
	FOREIGN KEY (label_id) REFERENCES tbl_label(id) ON DELETE CASCADE ,
	FOREIGN KEY (machine_id) REFERENCES tbl_machine(id) ON DELETE CASCADE,
	PRIMARY KEY (label_id, machine_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='machine label map';


/** user label map **/
CREATE TABLE IF NOT EXISTS tbl_user_label_map (
	user_id INTEGER,
	label_id INTEGER,
	FOREIGN KEY (label_id) REFERENCES tbl_label(id) ON DELETE CASCADE,
	PRIMARY KEY (user_id, label_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='user label map';

create table if not exists application_key (id INTEGER PRIMARY KEY AUTO_INCREMENT, public_key text not null, private_key text not null, passphrase text) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='application key';


DROP TABLE tbl_machine_status;

CREATE TABLE IF NOT EXISTS tbl_machine_status (
  machine_id INTEGER,
  user_id INTEGER,
  status_cd VARCHAR (1024) NOT NULL DEFAULT 'INITIAL',
  FOREIGN KEY (machine_id) REFERENCES tbl_machine(id) ON DELETE CASCADE,
  PRIMARY KEY (machine_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='machine status';

create table if not exists scripts (id INTEGER PRIMARY KEY AUTO_INCREMENT, user_id INTEGER, display_nm varchar(1024) not null, script text not null, foreign key (user_id) references users(id) on delete cascade) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='scripts';

create table if not exists public_keys (id INTEGER PRIMARY KEY AUTO_INCREMENT, key_nm varchar(1024) not null, type varchar(1024), fingerprint text, public_key text, enabled boolean not null default true, create_dt timestamp not null default CURRENT_TIMESTAMP(),  user_id INTEGER, profile_id INTEGER, foreign key (profile_id) references profiles(id) on delete cascade, foreign key (user_id) references users(id) on delete cascade) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='public keys';

/**
create table if not exists session_log (id BIGINT PRIMARY KEY AUTO_INCREMENT, user_id INTEGER, session_tm timestamp default CURRENT_TIMESTAMP, foreign key (user_id) references users(id) on delete cascade ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='session log';
*/
DROP TABLE tbl_session_log;
CREATE TABLE IF NOT EXISTS tbl_session_log (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  user_name VARCHAR (1024),
  session_id VARCHAR (1024),
  status_cd VARCHAR (1024),
  error_msg VARCHAR (1024),
  begin_dt DATETIME NOT NULL ,
  end_dt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP()
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='session log';

/**
create table if not exists terminal_log (session_id BIGINT, instance_id INTEGER, system_id INTEGER, output text not null, log_tm timestamp default CURRENT_TIMESTAMP, foreign key (session_id) references session_log(id) on delete cascade, foreign key (system_id) references system(id) on delete cascade) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='terminal log';
*/
DROP TABLE tbl_terminal_log;
CREATE TABLE IF NOT EXISTS tbl_terminal_log (
  session_id INTEGER,
  instance_id INTEGER,
  machine_id INTEGER,
  `output` TEXT NOT NULL,
  log_tm TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (session_id) REFERENCES tbl_session_log(id) ON DELETE CASCADE,
  FOREIGN KEY (machine_id) REFERENCES tbl_machine(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='terminal log';

CREATE TABLE IF NOT EXISTS tbl_command_record (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  user_id INTEGER,
  user_name VARCHAR (1024),
  command VARCHAR (1024),
  record_dt timestamp not null default CURRENT_TIMESTAMP()
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='command record';


/********************************** story: 机器更改为按照用户登录 ******************************************************/
/**
删除字段display_nm
 */
ALTER TABLE tbl_machine DROP COLUMN display_nm;
ALTER TABLE tbl_machine DROP COLUMN port;
ALTER TABLE tbl_machine DROP COLUMN authorized_keys;
ALTER TABLE tbl_machine DROP COLUMN user;
ALTER TABLE tbl_machine DROP COLUMN password;
ALTER TABLE tbl_machine DROP COLUMN status_cd;

/********************************** story: 异步拉取文件上传的状态 ******************************************************/
CREATE TABLE IF NOT EXISTS tbl_task (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  task_type VARCHAR (1024),
  nm VARCHAR (1024),
  `desc` VARCHAR (2048),
  status_cd VARCHAR (1024),
  begin_dt DATETIME NOT NULL ,
  end_dt timestamp not null default CURRENT_TIMESTAMP()
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='job task';

CREATE TABLE IF NOT EXISTS tbl_job (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  user_id INTEGER,
  job_type VARCHAR (1024),
  nm VARCHAR (1024),
  `desc` VARCHAR (2048),
  status_cd VARCHAR (1024),
  begin_dt DATETIME NOT NULL ,
  end_dt timestamp not null default CURRENT_TIMESTAMP()
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='job';

CREATE TABLE IF NOT EXISTS tbl_job_task_map (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  job_id INTEGER,
  task_id INTEGER
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='job task map';

/********************************** story: 异步打开terminal ******************************************************/
DROP TABLE tbl_machine_status;

DROP TABLE tbl_session_machine_status;

CREATE TABLE IF NOT EXISTS tbl_session_machine_status (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  session_id VARCHAR(1024),
  machine_id INTEGER,
  instance_id INTEGER,
  status_cd VARCHAR (1024) NOT NULL DEFAULT 'INITIAL',
  error_msg VARCHAR (1024),
  FOREIGN KEY (machine_id) REFERENCES tbl_machine(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='session machine status';


/********************************** story: 添加session log记录 ******************************************************/
DROP TABLE tbl_terminal_log;

DROP TABLE tbl_session_log;

CREATE TABLE IF NOT EXISTS tbl_session_log (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  user_name VARCHAR (1024),
  session_id VARCHAR (1024),
  status_cd VARCHAR (1024),
  error_msg VARCHAR (1024),
  begin_dt DATETIME NOT NULL ,
  end_dt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP()
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='session log';

CREATE TABLE IF NOT EXISTS tbl_terminal_log (
  session_id INTEGER,
  instance_id INTEGER,
  machine_id INTEGER,
  `output` TEXT NOT NULL,
  log_tm TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (session_id) REFERENCES tbl_session_log(id) ON DELETE CASCADE,
  FOREIGN KEY (machine_id) REFERENCES tbl_machine(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='terminal log';



