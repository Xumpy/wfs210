package be.velleman.nicowfs210;

import java.nio.charset.StandardCharsets;
import org.apache.log4j.Logger;


/**
 * @author bn
 * 
 */
public class Channel
{
	public String name;
	private InputCoupling inputCoupling;
	private VoltageDiv verticalDiv;
	private int verticalPosition;
	private byte[] samples = new byte[4096];
	public Boolean isNewData = false;
	private Boolean isX10 = false;

	/**
	 * This is the class that represents a Oscilloscope channel
	 */
        
        private static final Logger log = Logger.getLogger(Channel.class);
        
	public Channel()
	{
		super();
		for (int i = 0; i <= samples.length - 1; i++)
		{
			samples[i] = 0;
		}
	}

	public boolean isOverFlow()
	{
		for(int i =0;i< samples.length;i++)
		{
			int value = samples[i] & (0xff);
			if(value >= 252 || value <= 3)
			{
				return true;
			}
		}
		return false;
	
	}
	
	public int getVerticalPosition()
	{
		return verticalPosition;
	}

	public void setVerticalPosition(int verticalPosition)
	{
		this.verticalPosition = verticalPosition;
	}

	public Channel(String sName)
	{
		super();
		this.name = sName;
		for (int i = 0; i <= samples.length - 1; i++)
		{
			this.samples[i] = 0;
		}
	}

	public InputCoupling getInputCoupling()
	{
		return inputCoupling;
	}

	public void setInputCoupling(InputCoupling inputCoupling)
	{
		this.inputCoupling = inputCoupling;
	}

	public VoltageDiv getVerticalDiv()
	{
		return verticalDiv;
	}

	public void setVerticalDiv(VoltageDiv verticalDiv)
	{
		this.verticalDiv = verticalDiv;
	}

	/**
	 * Checks if the current channel is filled or not
	 * 
	 * @return is <code>true</code> if the channel settings are filled else
	 *         <code>false</false>
	 */
	public Boolean isFilled()
	{
		if (this.verticalDiv == null && this.inputCoupling == null)
		{
			return false;
		} else
		{
			return true;
		}
	}

	/**
	 * This will set new data at a given offset
	 * 
	 * @param offSet
	 *            the offset for the data
	 * @param data
	 *            the data that needs to be replaced
	 */
	public void setSampleData(int offSet, byte[] data)
	{
		int length = data.length - 1;
		for (int i = offSet; i <= length + offSet; i++)
		{
			if (i - offSet == 0)
			{
				this.samples[i] = data[i - offSet];
			}
			else
			{
				this.samples[i] = data[i - offSet - 1];
			}

		}
		if (offSet >= 2048)
			isNewData = true;

	}

	public Boolean getIsX10()
	{
		return isX10;
	}

	public void setIsX10(Boolean isX10)
	{
		this.isX10 = isX10;
	}

	public byte[] getSamples()
	{
		return this.samples;
	}
	
	public void clearSamples()
	{
		for(int i =0; i < samples.length; i++)
		{
			samples[i] = 0;
		}
	}
	public enum InputCoupling
	{
		AC, DC, GND
	}

        public void debug(){
            log.debug("name: " + name);
            log.debug("inputCoupling: " + inputCoupling);
            log.debug("verticalDiv: " + verticalDiv);
            log.debug("verticalPosition: " + verticalPosition);
            log.debug("samples: " + StaticBytes.toHex(samples));
            log.debug("isNewdata: " + isNewData);
            log.debug("isX10: " + isX10);
        }
        
        @Override
        public String toString(){
            return "name: " + name + 
                    ", inputCoupling: " + inputCoupling + 
                    ", verticalDiv: " + verticalDiv + 
                    ", verticalPosition: " + verticalPosition + 
                    ", samples: " + new String(samples) + 
                    ", isNewdata: " + isNewData + 
                    ", isX10: " + isX10;
        }
}
