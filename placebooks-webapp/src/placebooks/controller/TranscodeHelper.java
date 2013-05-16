package placebooks.controller;

import java.io.File;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.xuggle.xuggler.Converter;

/**
 * Helper class to encapasulate calls to xuggler/ffmpeg, so that it can be disabled in the
 * placebooks.properties file and ItemFactory does not throw an exception when loaded and xuggleris
 * not in the classpath
 * 
 * @author pszmp
 * 
 */
public class TranscodeHelper
{
	private static final Logger log = Logger.getLogger(ItemFactory.class.getName());

	/**
	 * Use xuggler to transcode the video to a Chroime compatible format and save as the same name
	 * with x on the end.
	 * 
	 * @param String
	 *            Input file name will be transcoded to inputfilex.ogg
	 * @param String
	 *            The name of the output file
	 */
	public static void transcodeVideoForChrome(final String inputfilename)
	{
		log.debug("Transcoding video for Chrome: " + inputfilename);
		final String outputfilename = inputfilename + "-chrome.ogg";
		final String[] options = { "-acodec", "libvorbis" };
		transcodeVideo(inputfilename, outputfilename, options);
	}

	/**
	 * Use xuggler to transcode the video to an Android compatible format and save as the same name
	 * with "-mobile.mp4".
	 * 
	 * @param String
	 *            Input file name will be transcoded to inputfile-mobile.mp4
	 */
	public static void transcodeVideoForMobile(final String inputfilename)
	{
		log.debug("Transcoding video for mobile: " + inputfilename);
		final String outputfilename = inputfilename + "-mobile.mp4";
		final String[] options = {};
		transcodeVideo(inputfilename, outputfilename, options);
	}

	/**
	 * Use xuggler encoder to transcode a fideo from input file name to output file name with the
	 * given options.
	 * 
	 * @param input
	 * @param output
	 * @param options
	 * @return
	 */
	private static void transcodeVideo(final String input, final String output, final String[] options)
	{
		final File outputFile = new File(output);
		if (!outputFile.exists())
		{
			final File inputfile = new File(input);
			if (inputfile.exists())
			{
				// This is the converter object we will use.
				final Converter converter = new Converter();

				// Assemble the options array
				// This is what's passed to the ffmpeg command line call...
				final ArrayList<String> arguments = new ArrayList<String>();
				arguments.add(input);
				for (final String arg : options)
				{
					arguments.add(arg);
				}
				arguments.add(output);

				try
				{
					String[] opts = {};
					opts = arguments.toArray(opts);
					// Finally, we run the transcoder with the options we provided.
					converter.run(converter.parseOptions(converter.defineOptions(), opts));
				}
				catch (final Exception e)
				{
					log.error(e.getMessage());
					e.printStackTrace();
				}
				log.debug("Transcoding of " + input + " complete.");
			}
			else
			{
				log.error("Not transcoding " + input + ": File not found!");
			}
		}
		else
		{
			log.debug("Transcoding video not needed - file eists " + output);
		}
	}

}
