package com.yanan.test;

import java.io.IOException;

import com.yanan.utils.resource.ResourceManager;
import com.yanan.utils.resource.scanner.Path;

public class Del {
	public static void main(String[] args) throws IOException {
		Path path = new Path(ResourceManager.projectPath());
		path.scanner((file)->{
			if(file.getName().startsWith("._") && file.delete())
			System.out.println(file);
		});
	}
}
