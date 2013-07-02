package com.openceres.util;

import java.util.UUID;

public class UUIDGenerator {

	public synchronized static UUID gnerateUUID()
	{
		return UUID.randomUUID();
	}
}
