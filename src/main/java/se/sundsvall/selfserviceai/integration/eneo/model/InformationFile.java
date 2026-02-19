package se.sundsvall.selfserviceai.integration.eneo.model;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class InformationFile implements MultipartFile {

	private static final String FILE_NAME = "selfservice-ai-data.json";

	private byte[] data;

	public static InformationFile create() {
		return new InformationFile();
	}

	public InformationFile withData(final String data) {
		this.data = isNull(data) ? null : data.getBytes(Charset.defaultCharset());
		return this;
	}

	@Override
	public String getName() {
		return FILE_NAME;
	}

	@Override
	public String getOriginalFilename() {
		return getName();
	}

	@Override
	public String getContentType() {
		return APPLICATION_JSON_VALUE;
	}

	@Override
	public boolean isEmpty() {
		return isNull(data) || data.length == 0;
	}

	@Override
	public long getSize() {
		return isNull(data) ? 0 : data.length;
	}

	@Override
	public byte[] getBytes() {
		return data;
	}

	@Override
	public InputStream getInputStream() {
		return isNull(data) ? null : new ByteArrayInputStream(data);
	}

	@Override
	public void transferTo(final File destination) throws IOException, IllegalStateException {
		if (nonNull(data)) {
			FileUtils.writeByteArrayToFile(destination, data);
		}
	}
}
