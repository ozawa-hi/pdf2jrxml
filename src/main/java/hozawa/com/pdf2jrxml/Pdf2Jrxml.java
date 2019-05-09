package hozawa.com.pdf2jrxml;

import net.sf.jasperreports.engine.JRException;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Pdf2Jrxml
 *
 */
public class Pdf2Jrxml {
	private final static int OK_CODE = 0;
	private final static int ERROR_CODE = 1;	

    public static void main( String[] args ) {
    	String pdfFilename = "";
    	String jrxmlFilename = "";
    	
    	JrxmlReport report = new JrxmlReport();
    	
		Options options = new Options();
		final Option pdfFilenameOption = Option.builder("i")
					.longOpt("input")
					.type(String.class)
					.required(true)
					.numberOfArgs(1)
					.desc("pdf file to parse")
					.build();
		final Option jrxmlFilenameOption = Option.builder("o")
				.longOpt("output")
				.type(String.class)
				.required(true)
				.numberOfArgs(1)
				.desc("name of jrxml file to generate")
				.build();
		Option helpOption = Option.builder("h")
				.longOpt("help")
				.required(false)
				.desc("show this message")
				.build();
		
		options.addOption(pdfFilenameOption);
		options.addOption(jrxmlFilenameOption);
		options.addOption(helpOption);
    	
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
			
			// show help
			if (cmd.hasOption("h")) {
				help(options);
				System.exit(OK_CODE);
			}
			
			if (cmd.hasOption("i")) {
				pdfFilename = cmd.getOptionValue("i");
			}
			if (cmd.hasOption("o")) {
				jrxmlFilename = cmd.getOptionValue("o");
			}
		
			// generate jrxml from pdf file
			report.generateJrxml(pdfFilename, jrxmlFilename);
		} catch (ParseException e) {
			help(options);
		} catch (JRException e) {
			e.printStackTrace();
		}
    	
    	System.out.println("Finished.\ninput pdf filename:" + pdfFilename + "\noutput jrxml filename:" + jrxmlFilename);
    }
		
	private static void help(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("Pdf2Jrxml", options, true);
	}
}
