package br.com.topsys.auditoria;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import br.com.topsys.util.TSPropertiesUtil;
import br.com.topsys.util.TSUtil;

public final class TSAuditoria {

	private static final String DATA_HORA_MINUTOS = "dd/MM/yyyy HH:mm:ss";
	private static final String DATA = "dd-MM-yyyy";

	private static final String AUDITORIA_LOG_QUERY_MAX_SECONDS = "auditoria.log.query.max.seconds";

	private static final String AUDITORIA_LOG_PATH_FILE = "auditoria.log.path.file";

	private Instant instant;

	private String query;
	private String parametrosQuery;

	private String pathFile;

	private long maxSeconds;

	public TSAuditoria(String query, Object[] parametros) {
		this.query = query;
		this.parametrosQuery = Arrays.toString(parametros);
	}

	public void begin() {
		this.instant = Instant.now();

	}

	public void end() {

		if (this.hasConfig()) {

			Duration duration = Duration.between(this.instant, Instant.now());

			long duracaoSegundos = duration.toSeconds(); 
			

			if (duracaoSegundos >= this.maxSeconds) {

				this.saveInFile(duracaoSegundos);

			}

		}

	}

	private boolean hasConfig() {
		try {

			this.pathFile = TSPropertiesUtil.getInstance().getProperty(AUDITORIA_LOG_PATH_FILE);
			this.maxSeconds = Long.parseLong(TSPropertiesUtil.getInstance().getProperty(AUDITORIA_LOG_QUERY_MAX_SECONDS));

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return !TSUtil.isEmpty(this.pathFile) && !TSUtil.isEmpty(this.maxSeconds);
	}

	private void saveInFile(final long duracaoSegundos) {

		Path path = Paths.get(this.pathFile + LocalDate.now().format(DateTimeFormatter.ofPattern(DATA)) + ".csv");

		StringBuilder builder = new StringBuilder();
        
		String dataFormatada = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATA_HORA_MINUTOS));
		
		builder.append("[")
				.append(dataFormatada)
				.append("];")
				.append(this.query)
				.append(";")
				.append(this.parametrosQuery)
				.append(";")
				.append(duracaoSegundos)
				.append(";")
				.append(System.getProperty("line.separator"));

		try {

			Files.write(path, builder.toString().getBytes(),
					StandardOpenOption.CREATE, StandardOpenOption.APPEND);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
