package com.timwe.batch.cdr;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.item.file.transform.AbstractLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * A custom {@link LineTokenizer} implementation that based on {@link DelimitedLineTokenizer} 
 * but removed the quoteCharacter functionality as it produces errors when a line consist of
 * double quote. Also implemented a function that resolve the delimited character appear in message field 
 * 
 * @author cheehoo
 * @since 1.0.0
 */
public class CustomDelimitedLineTokenizer extends AbstractLineTokenizer  {
	
	/**
	 * Convenient constant for the common case of a tab delimiter.
	 */
	public static final char DELIMITER_TAB = '\t';

	/**
	 * Convenient constant for the common case of a comma delimiter.
	 */
	public static final char DELIMITER_COMMA = ',';

	/**
	 * Convenient constant for the common case of a " character used to escape delimiters or line endings.
	 */
	public static final char DEFAULT_QUOTE_CHARACTER = '"';

	// the delimiter character used when reading input.
	private char delimiter;

	private char quoteCharacter = DEFAULT_QUOTE_CHARACTER;

	private String quoteString;

	/**
	 * Create a new instance of the {@link CustomDelimitedLineTokenizer} class for the common case where the delimiter is a
	 * {@link #DELIMITER_COMMA comma}.
	 * 
	 * @see #CustomDelimitedLineTokenizer(char)
	 * @see #DELIMITER_COMMA
	 */
	public CustomDelimitedLineTokenizer() {
		this(DELIMITER_COMMA);
	}

	/**
	 * Create a new instance of the {@link CustomDelimitedLineTokenizer} class.
	 * 
	 * @param delimiter the desired delimiter
	 */
	public CustomDelimitedLineTokenizer(char delimiter) {
		Assert.state(delimiter != DEFAULT_QUOTE_CHARACTER, "[" + DEFAULT_QUOTE_CHARACTER
		        + "] is not allowed as delimiter for tokenizers.");

		this.delimiter = delimiter;
		setQuoteCharacter(DEFAULT_QUOTE_CHARACTER);
	}

	/**
	 * Setter for the delimiter character.
	 * 
	 * @param delimiter
	 */
	public void setDelimiter(char delimiter) {
		this.delimiter = delimiter;
	}

	/**
	 * Public setter for the quoteCharacter. The quote character can be used to extend a field across line endings or to
	 * enclose a String which contains the delimiter. Inside a quoted token the quote character can be used to escape
	 * itself, thus "a""b""c" is tokenized to a"b"c.
	 * 
	 * @param quoteCharacter the quoteCharacter to set
	 * 
	 * @see #DEFAULT_QUOTE_CHARACTER
	 */
	public void setQuoteCharacter(char quoteCharacter) {
		this.quoteCharacter = quoteCharacter;
		this.quoteString = "" + quoteCharacter;
	}

	/**
	 * Yields the tokens resulting from the splitting of the supplied <code>line</code>.
	 * If the message token consists of the delimiter eg : | then it will resolve automatically by adjusting
	 * the token position
	 * 
	 * @param line the line to be tokenized
	 * 
	 * @return the resulting tokens
	 * 
	 */
	@Override
	protected List doTokenize(String line) {	
		List tokens = new ArrayList();
		List actualToken = new ArrayList();
		
		// line is never null in current implementation
		// line is checked in parent: AbstractLineTokenizer.tokenize()
		char[] chars = line.toCharArray();
		boolean inQuoted = false;
		int lastCut = 0;
		int expectedTokenSize = 44;
		int columnContainsDelimiter = 7;
		
		tokens = processToken(chars, inQuoted, lastCut);
		
		//check if token more than expected (this is due to the msg field may have deliberately put a delimiter)		
		if(tokens.size() > expectedTokenSize)
		{		
			actualToken = mergeTokensContainDelimiters(tokens, expectedTokenSize, columnContainsDelimiter);
		}
		else
		{
			actualToken = tokens;
		}
		
		return actualToken;
	}
	
	/**
	 * Yields the tokens resulting from the splitting of the supplied <code>chars[]</code>.  
	 * Removed the quoteCharacter checking that determine the inQuoted.
	 * 
	 * @param chars the char array to be tokenize
	 * @param inQuoted indicate the line is quoted
	 * @param lastCut last char before the delimiter
	 * 
	 * @return the resulting token
	 */
	private List processToken(char chars[], boolean inQuoted, int lastCut)
	{
		List tokens = new ArrayList();
		int length = chars.length;
		for (int i = 0; i < length; i++) {
			char currentChar = chars[i];
			boolean isEnd = (i == (length - 1));
				
			if ((isDelimiterCharacter(currentChar) && !inQuoted) || isEnd) {
				int endPosition = (isEnd ? (length - lastCut) : (i - lastCut));

				if (isEnd && isDelimiterCharacter(currentChar)) {
					endPosition--;
				}

				String value = null;
				value = maybeStripQuotes(new String(chars, lastCut, endPosition));
				tokens.add(value);

				if (isEnd && (isDelimiterCharacter(currentChar))) {
					tokens.add("");
				}

				lastCut = i + 1;
//			} else if (isQuoteCharacter(currentChar)) {
//				inQuoted = !inQuoted;
			}

		}
		return tokens;
	}
	
	/**
	 * Reposition the tokens into expectedTokenSize as the a column may have characters that
	 * same as delimiter. 
	 * 
	 * @param tokens the tokens that tokenized from line
	 * @param expectedTokenSize the expected token size
	 * @param columnContainsDelimiter column that contain the delimiter
	 * 
	 * @return the repositioned tokens
	 * 
	 */
	private List mergeTokensContainDelimiters(List tokens, int expectedTokenSize, int columnContainsDelimiter)
	{
		List reAdjustedTokens = new ArrayList();
		int additionalTokenSize = tokens.size() - expectedTokenSize; 			
		
		for(int i =0; i < tokens.size() ; i++)
		{
			if(i == columnContainsDelimiter)
			{
				StringBuilder message = new StringBuilder();
				for(int y = 0; y <= additionalTokenSize ; y++)
				{
					message.append(tokens.get(columnContainsDelimiter+y));
					if(y != additionalTokenSize)
					{
							message.append(this.delimiter);
					}
				}
				reAdjustedTokens.add(message.toString());
				i = i+additionalTokenSize;
				
			}
			else
			{
				reAdjustedTokens.add(tokens.get(i));
			}
		}
		return reAdjustedTokens;
	}

	/**
	 * If the string is quoted strip (possibly with whitespace outside the quotes (which will be stripped), replace
	 * escaped quotes inside the string. Quotes are escaped with double instances of the quote character.
	 * 
	 * @param string
	 * @return the same string but stripped and unescaped if necessary
	 */
	private String maybeStripQuotes(String string) {
		String value = string.trim();
		if (isQuoted(value)) {
			value = StringUtils.replace(value, "" + quoteCharacter + quoteCharacter, "" + quoteCharacter);
			int endLength = value.length() - 1;
			// used to deal with empty quoted values
			if (endLength == 0) {
				endLength = 1;
			}
			string = value.substring(1, endLength);
		}
		return string;
	}

	/**
	 * Is this string surrounded by quite characters?
	 * 
	 * @param value
	 * @return true if the value starts and ends with the {@link #quoteCharacter}
	 */
	private boolean isQuoted(String value) {
		if (value.startsWith(quoteString) && value.endsWith(quoteString)) {
			return true;
		}
		return false;
	}

	/**
	 * Is the supplied character the delimiter character?
	 * 
	 * @param c the character to be checked
	 * @return <code>true</code> if the supplied character is the delimiter character
	 * @see #CustomDelimitedLineTokenizer(char)
	 */
	private boolean isDelimiterCharacter(char c) {
		return c == this.delimiter;
	}

	/**
	 * Is the supplied character a quote character?
	 * 
	 * @param c the character to be checked
	 * @return <code>true</code> if the supplied character is an quote character
	 * @see #setQuoteCharacter(char)
	 */
	protected boolean isQuoteCharacter(char c) {
		return c == quoteCharacter;
	}
	

}
