/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package l2server.gameserver.instancemanager;

import l2server.L2DatabaseFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Kilian, Pere
 */
public class SurveyManager
{
	private static SurveyManager instance;

	private final String GET_CURRENT_SURVEY =
			"SELECT survey_id,question,description FROM survey WHERE survey_id = (SELECT MAX(survey_id) FROM survey where active = 1)";
	private final String GET_CURRENT_SURVEY_POSSIBLE_ANSWERS =
			"SELECT answer_id,answer FROM survey_possible_answer WHERE survey_id = ?";
	private final String GET_CURRENT_SURVEY_ANSWERS = "SELECT charId FROM survey_answer WHERE survey_id = ?";
	private final String STORE_ANSWER = "INSERT INTO survey_answer (charId,survey_id,answer_id) VALUES (?,?)";

	private int id = 0;
	private String question;
	private String description;
	private Map<Integer, String> possibleAnswers;
	private List<Integer> answers;

	private SurveyManager()
	{
		load();
	}

	private void load()
	{
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(GET_CURRENT_SURVEY);
			ResultSet rset = statement.executeQuery();

			if (rset.next())
			{
				this.id = rset.getInt("survey_id");
				this.question = rset.getString("question");
				this.description = rset.getString("description");

				PreparedStatement statement2 = con.prepareStatement(GET_CURRENT_SURVEY_POSSIBLE_ANSWERS);
				statement2.setInt(1, this.id);
				ResultSet rset2 = statement2.executeQuery();
				Map<Integer, String> possibleAnswers = new HashMap<>();
				while (rset2.next())
				{
					possibleAnswers.put(rset.getInt("answer_id"), rset.getString("answer"));
				}

				this.possibleAnswers = possibleAnswers;

				statement2 = con.prepareStatement(GET_CURRENT_SURVEY_ANSWERS);
				statement2.setInt(1, this.id);
				rset2 = statement2.executeQuery();
				List<Integer> answers = new ArrayList<>();
				while (rset2.next())
				{
					answers.add(rset.getInt("charId"));
				}

				this.answers = answers;
			}

			rset.close();
			statement.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}

	public boolean isActive()
	{
		return this.id > 0;
	}

	public String getQuestion()
	{
		return this.question;
	}

	public String getDescription()
	{
		return this.description;
	}

	public Integer[] getPossibleAnswerIds()
	{
		return (Integer[]) this.possibleAnswers.keySet().toArray();
	}

	public String getPossibleAnswer(int id)
	{
		return this.possibleAnswers.get(id);
	}

	public boolean playerAnswered(int playerObjId)
	{
		return this.answers.contains(playerObjId);
	}

	public boolean storeAnswer(int playerObjId, int answerIndex)
	{
		if (this.answers.contains(playerObjId))
		{
			return false;
		}
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(STORE_ANSWER);
			statement.setInt(1, playerObjId);
			statement.setInt(2, answerIndex);
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
		this.answers.add(playerObjId);
		return true;
	}

	public static SurveyManager getInstance()
	{
		if (instance == null)
		{
			instance = new SurveyManager();
		}
		return instance;
	}
}
