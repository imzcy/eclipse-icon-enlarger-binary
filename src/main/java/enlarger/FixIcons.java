package enlarger;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.imgscalr.Scalr;

import com.mortennobel.imagescaling.AdvancedResizeOp;
import com.mortennobel.imagescaling.ResampleOp;

/**
 * Small utility to double the size of eclipse icons for QHD monitors.
 * 
 * @author David Levy
 * @since 2014/04/10
 *
 */
public class FixIcons {

	private static final Logger logger = Logger.getLogger(FixIcons.class);
	
	private static Options options = new Options();
	
	static
	{
		Option baseDir = new Option("b", "baseDir", true,
				"This is the base directory where we'll parse jars/zips");
		Option outputDir = new Option("o", "outputDir", true,
				"This is the base directory where we'll place output");
		baseDir.setRequired(true);
		outputDir.setRequired(true);
		
		options.addOption(baseDir);
		options.addOption(outputDir);
	}

	public static final void main(String[] args) {

			try {
			GnuParser parser = new GnuParser();
			CommandLine commandLine = parser.parse(options, args);
			if(!commandLine.hasOption("b") || !commandLine.hasOption("o"))
			{
				printHelp();
				return;
			}
			
			String baseDirArg = commandLine.getOptionValue("b");
			logger.info("Base directory: " + baseDirArg);

			String outputDirArg = commandLine.getOptionValue("o");
			logger.info("Output directory: " + outputDirArg);

			File base = new File(baseDirArg);
			if (!base.exists() || !base.canRead() || !base.isDirectory()) {
				logger.error("Unable to read from base directory");
				return;
			}

			File output = new File(outputDirArg);
			if(!output.exists())
			{
				if(!output.mkdirs())
				{
					logger.error("Can't create directory '"+outputDirArg+"'");
					printHelp();
					return;
				}
			}
			if (!output.exists() || !output.canRead() || !output.canWrite()
					|| !output.isDirectory()) {
				logger.error("Unable to write to output director");
				return;
			}

			if (base.list() == null || base.list().length == 0) {
				logger.error("The base directory is empty");
				return;
			}

			if (output.list() != null && output.list().length != 0) {
				logger.error("The output directory is not empty");
				return;
			}

			new FixIconsProcessor().processDirectory(base, output);

		} catch (ParseException e) {
			logger.error("Unable to parse arguments: " + e.getMessage());
			printHelp();
		} catch (Exception e) {
			logger.error("Unexpected error: " + e.getMessage(), e);
			printHelp();
		}
	}
	
	private static void printHelp()
	{
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp( "enlarger", options );
	}
}
