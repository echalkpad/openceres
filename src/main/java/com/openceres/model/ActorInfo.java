package com.openceres.model;

import java.util.Date;

import org.codehaus.jackson.map.annotate.JsonRootName;

import com.openceres.core.common.ActorRole;
import com.openceres.core.common.ActorStatus;

@JsonRootName("actor")
public class ActorInfo {
	
	String uri;
	ActorRole role = ActorRole.NONE;
	ActorStatus status = ActorStatus.INIT;
	Date 	start;
	String 	command;
	String	description;
	
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public ActorRole getRole() {
		return role;
	}
	public void setRole(ActorRole role) {
		this.role = role;
	}
	public ActorStatus getStatus() {
		return status;
	}
	public void setStatus(ActorStatus status) {
		this.status = status;
	}
	public Date getStart() {
		return start;
	}
	public void setStart(Date start) {
		this.start = start;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String toJson()
	{
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("{");
		stringBuffer.append("\"uri\":\"").append(uri).append("\",");
		stringBuffer.append("\"role\":\"").append(role.toString()).append("\",");
		stringBuffer.append("\"status\":\"").append(status.toString()).append("\",");
		stringBuffer.append("\"start\":").append(start.getTime()).append(",");
		stringBuffer.append("\"command\":\"").append(command).append("\",");
		stringBuffer.append("\"description\":\"").append(description).append("\"");;
		stringBuffer.append("}");
		
		return stringBuffer.toString();
	}
}
