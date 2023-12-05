CREATE TABLE IF NOT EXISTS lecturer(
  id INT AUTO_INCREMENT PRIMARY KEY ,
  name VARCHAR(250) NOT NULL ,
  designation VARCHAR(600) NOT NULL ,
  qualifications VARCHAR(600) NOT NULL ,
  picture VARCHAR(200) DEFAULT NULL,
  linkedin VARCHAR(2000) DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS full_time_rank(
lecture_id INT NOT NULL ,
`rank` INT NOT NULL ,
CONSTRAINT pk PRIMARY KEY (lecture_id,`rank`),
CONSTRAINT fk FOREIGN KEY (lecture_id) REFERENCES lecturer(id)
);

CREATE TABLE IF NOT EXISTS part_time_rank(
    lecture_id INT NOT NULL ,
    `rank` INT NOT NULL ,
    CONSTRAINT pk PRIMARY KEY (lecture_id,`rank`),
    CONSTRAINT fk_p FOREIGN KEY (lecture_id) REFERENCES lecturer(id)
);
SELECT l.id, l.name, ftr.`rank`, ptr.`rank` FROM lecturer l left outer join full_time_rank ftr on l.id = ftr.lecture_id
    LEFT OUTER JOIN part_time_rank ptr on l.id = ptr.lecture_id WHERE l.id = 3;

UPDATE lecturer SET  name = 'sukan' WHERE id = 3;
SELECT l.id,l.picture as lecPicture, ftr.`rank` AS ftr, ptr.`rank` AS ptr FROM lecturer l left outer join full_time_rank ftr on l.id = ftr.lecture_id LEFT OUTER JOIN part_time_rank ptr on l.id = ptr.lecture_id WHERE l.id = 3;