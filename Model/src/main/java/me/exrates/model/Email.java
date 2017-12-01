package me.exrates.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.File;
import java.util.List;

@Getter @Setter
@ToString
public class Email {

	private String to;
	private String from;
	private String message;
	private String subject;
	private List<File> attachments;


}
