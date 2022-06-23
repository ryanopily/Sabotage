package ml.zer0dasho.plumber.game;

import java.util.Map;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.google.common.collect.Maps;

import ml.zer0dasho.plumber.utils.Sprink;

/**
 * Used to build GUIs using native minecraft scoreboards.
 * @author 0-o#9646
 */
public class ScoreMenu {

	public final Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
	
	private final Map<String, Team> teams = Maps.newHashMap();
	private final Map<DisplaySlot, Objective> objectives = Maps.newHashMap();
	private final Map<DisplaySlot, Map<Integer, Score>> scores = Maps.newHashMap();
	
	/**
	 * Create a new scoreboard objective (used to track points).
	 * A criteria of 'dummy' is used for custom GUIs.
	 *
	 * @param displayName - Name of the objective
	 * @param criteria    - Determines how the scores update
	 * @param displaySlot - Where the scoreboard is displayed
	 * @return ScoreMenu
	 */
	public ScoreMenu newObjective(String displayName, String criteria, DisplaySlot displaySlot) {
		
		// Overwrite old objectives
		Optional.ofNullable(objectives.get(displaySlot)).ifPresent(Objective::unregister);
		
		Objective objective = scoreboard.registerNewObjective(displayName, criteria);
		objective.setDisplayName(displayName);
		objective.setDisplaySlot(displaySlot);
		
		objectives.put(displaySlot, objective);
		return this;
	}
	
	/**
	 * Create a new team.
	 * 
	 * @param displayName - Name of the team
	 * @return ScoreMenu
	 */
	public ScoreMenu newTeam(String displayName) {
		return newTeam(displayName, null, null);
	}
	
	/**
	 * Create a new team.
	 *
	 * @param displayName - Display name of the team
	 * @param prefix	  - Team prefix
	 * @param suffix 	  - Team suffix
	 * @return ScoreMenu
	 */
	public ScoreMenu newTeam(String displayName, String prefix, String suffix) {
		return newTeam(displayName, prefix, suffix, null, null);
	}
	
	/**
	 * 
	 * Create a new team.
	 *
	 * @param displayName  - Display name of the new team
	 * @param prefix 	   - Team prefix (Shows before player name)
	 * @param suffix 	   - Team suffix (Shows after player name)
	 * @param seeInvis 	   - See invisible players on the same team
	 * @param friendlyFire - Allow players on the same team to kill each other
	 * @return ScoreMenu
	 */
	public ScoreMenu newTeam(String displayName, String prefix, String suffix, Boolean seeInvis, Boolean friendlyFire) {
		
		// Overwrite old teams
		Optional.ofNullable(teams.get(displayName)).ifPresent(Team::unregister);
		
		Team team = scoreboard.registerNewTeam(displayName);
			 team.setDisplayName(displayName);
			 if(prefix != null) 	  team.setPrefix(Sprink.color(prefix));
		   	 if(suffix != null) 	  team.setSuffix(Sprink.color(suffix));
			 if(seeInvis != null) 	  {
				  team.setCanSeeFriendlyInvisibles(seeInvis);
				  // team.setOption(Team.Option.NAME_TAG_VISIBILITY, seeInvis ? Team.OptionStatus.ALWAYS : Team.OptionStatus.NEVER);
			 }
			 if(friendlyFire != null) team.setAllowFriendlyFire(friendlyFire);

		teams.put(displayName, team);
		return this;
	}
	
	public Team getTeam(String id) {
		return teams.get(id);
	}
	
	/**
	 * Create a new score entry.
	 * 
	 * @param slot  - Objective to bind the score to
	 * @param text  - Set the text score
	 * @param level - 0 is the bottom slot, max is the top slot
	 * @return ScoreMenu
	 */
	public ScoreMenu newScore(DisplaySlot slot, String text, int level) {
		
		// Overwrite old scores
		scores.computeIfAbsent(slot, (s) -> Maps.newHashMap());
		Optional.ofNullable(scores.get(slot).get(level)).ifPresent(score -> scoreboard.resetScores(score.getEntry()));

		Score score = objectives.get(slot).getScore(text);
		score.setScore(level);
		
		scores.get(slot).put(level, score);
		return this;
	}	
}