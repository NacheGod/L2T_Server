/*
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */

package l2server.gameserver.script.faenor;

import l2server.gameserver.ThreadPoolManager;
import l2server.gameserver.script.*;
import l2server.log.Log;
import org.w3c.dom.Node;

import javax.script.ScriptContext;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Luis Arias
 */
public class FaenorEventParser extends FaenorParser
{
	static Logger log = Logger.getLogger(FaenorEventParser.class.getName());
	private DateRange eventDates = null;

	@Override
	public void parseScript(final Node eventNode, ScriptContext context)
	{
		String ID = attribute(eventNode, "ID");

		if (DEBUG)
		{
			Log.fine("Parsing Event \"" + ID + "\"");
		}

		this.eventDates = DateRange.parse(attribute(eventNode, "Active"), DATE_FORMAT);

		Date currentDate = new Date();
		if (this.eventDates.getEndDate().before(currentDate))
		{
			Log.info("Event ID: (" + ID + ") has passed... Ignored.");
			return;
		}

		if (this.eventDates.getStartDate().after(currentDate))
		{
			Log.info("Event ID: (" + ID + ") is not active yet... Ignored.");
			ThreadPoolManager.getInstance().scheduleGeneral(() -> parseEventDropAndMessage(eventNode),
					this.eventDates.getStartDate().getTime() - currentDate.getTime());
			return;
		}

		parseEventDropAndMessage(eventNode);
	}

	protected void parseEventDropAndMessage(Node eventNode)
	{

		for (Node node = eventNode.getFirstChild(); node != null; node = node.getNextSibling())
		{

			if (isNodeName(node, "DropList"))
			{
				parseEventDropList(node);
			}
			else if (isNodeName(node, "Message"))
			{
				parseEventMessage(node);
			}
		}
	}

	private void parseEventMessage(Node sysMsg)
	{
		if (DEBUG)
		{
			Log.fine("Parsing Event Message.");
		}

		try
		{
			String type = attribute(sysMsg, "Type");
			String[] message = attribute(sysMsg, "Msg").split("\n");

			if (type.equalsIgnoreCase("OnJoin"))
			{
				this.bridge.onPlayerLogin(message, this.eventDates);
			}
		}
		catch (Exception e)
		{
			Log.log(Level.WARNING, "Error in event parser: " + e.getMessage(), e);
		}
	}

	private void parseEventDropList(Node dropList)
	{
		if (DEBUG)
		{
			Log.fine("Parsing Droplist.");
		}

		for (Node node = dropList.getFirstChild(); node != null; node = node.getNextSibling())
		{
			if (isNodeName(node, "AllDrop"))
			{
				parseEventDrop(node);
			}
		}
	}

	private void parseEventDrop(Node drop)
	{
		if (DEBUG)
		{
			Log.fine("Parsing Drop.");
		}

		try
		{
			int[] items = IntList.parse(attribute(drop, "Items"));
			int[] count = IntList.parse(attribute(drop, "Count"));
			double chance = getPercent(attribute(drop, "Chance"));

			this.bridge.addEventDrop(items, count, chance, this.eventDates);
		}
		catch (Exception e)
		{
			Log.log(Level.WARNING, "ERROR(parseEventDrop):" + e.getMessage(), e);
		}
	}

	static class FaenorEventParserFactory extends ParserFactory
	{
		@Override
		public Parser create()
		{
			return new FaenorEventParser();
		}
	}

	static
	{
		ScriptEngine.parserFactories.put(getParserName("Event"), new FaenorEventParserFactory());
	}
}
