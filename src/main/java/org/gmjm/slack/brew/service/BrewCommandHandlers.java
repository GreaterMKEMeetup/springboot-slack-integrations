package org.gmjm.slack.brew.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.FastDateFormat;
import org.gmjm.slack.api.message.SlackMessageBuilder;
import org.gmjm.slack.api.message.SlackMessageFactory;
import org.gmjm.slack.brew.repositories.BrewRepository;
import org.gmjm.slack.core.message.UserRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import org.gmjm.slack.brew.domain.Brew;

@Service
public class BrewCommandHandlers
{

	//private static final String BREW_MASTER = "<@U03JHGAP3|Nick>";

	@Value("${slack.brew.brew_master_id}")
	private String brewMasterId;

	@Value("${slack.brew.brew_master_username}")
	private String brewMasterUsername;

	private static final Logger logger = LoggerFactory.getLogger(BrewCommandHandlers.class);

	private static FastDateFormat fdf = FastDateFormat.getInstance("EEE, MMM d @ h:mm a", TimeZone.getTimeZone("CST"),null);

	@Autowired
	private SlackMessageFactory slackMessageFactory;

	private Map<String,Function<BrewRequestContext,SlackMessageBuilder>> commands = new HashMap<>();

	public BrewCommandHandlers() {
		commands.put("debug_ephemeral", this::debug);
		commands.put("brew_ephemeral", this::brewPrivate);
		commands.put("gone_ephemeral", this::gonePrivate);
		commands.put("today_ephemeral", this::today);
		commands.put("help_ephemeral", this::help);
		commands.put("supersecretreset_ephemeral", this::reset);
		commands.put("_ephemeral", this::brewStatus);

		commands.put("brew_public", this::brew);
		commands.put("gone_public", this::gone);
		commands.put("_public", this::consume);
	}

	public SlackMessageBuilder handle(String handlerName, BrewRequestContext brc)
	{
		Function<BrewRequestContext,SlackMessageBuilder> handler = commands.get(handlerName);

		if(handler == null) {
			throw new RuntimeException("No handler fond for command: " + handlerName);
		}

		return handler.apply(brc);
	}

	SlackMessageBuilder help(BrewRequestContext brc) {
		logger.info("Help");

		SlackMessageBuilder builder = slackMessageFactory.createMessageBuilder();

		StringBuilder sb = new StringBuilder();

		sb.append("---commands---").append("\n")
		.append("> -- A blank will show you if there is any coffee available. ").append("\n")
		.append("> *brew {Type of coffee you brewed.}* -- Adds a fresh pot of coffee.").append("\n")
		.append("> *today* -- List all the coffee that was brewed today.").append("\n")
		.append("> *gone* -- Sets the current pots to gone, and lets Nick know to make another pot =)").append("\n");

		return builder.setText(sb.toString());
	}


	SlackMessageBuilder today(BrewRequestContext brc)
	{
		logger.info("Getting Today's brews");

		SlackMessageBuilder builder = slackMessageFactory.createMessageBuilder();

		List<Brew> results = getTodaysBrews(brc.brewRepository);
		if (results.size() == 0)
		{
			return builder.setText(String.format("No coffee brewed yet, %s go make some!", getBrewMasterRef()));
		}

		String message = results.stream()
			.map(this::pretty)
			.reduce((a,b) -> a + "\n" + b)
			.orElse("");

		return builder.setText(message);

	}


	SlackMessageBuilder brewStatus(BrewRequestContext brc)
	{
		logger.info("Getting Status");

		List<Brew> results = brc.brewRepository.findFirstByOrderByBrewDateDesc();
		if (results.size() == 0)
		{
			return slackMessageFactory.createMessageBuilder().setText("No coffee brewed yet, go make some!");
		}
		Brew lastBrew = results.get(0);

		long minutes = ((System.currentTimeMillis() / 60000) - (lastBrew.getBrewDate().getTime() / 60000));

		String status = String.format("%s was brewed %s minutes ago by %s.", lastBrew.getBrewName(), minutes, lastBrew.getBrewedBy());
		return slackMessageFactory.createMessageBuilder().setText(status);
	}


	SlackMessageBuilder brew(BrewRequestContext brc)
	{
		logger.info("Brewing");

		String brewName = brc.brewCommand.text;

		if(StringUtils.isEmpty(brewName))
		{
			return slackMessageFactory.createMessageBuilder()
				.setText("Include the name of the coffee you brewed! Example, /coffee brew Blue Heeler");
		}

		Brew newBrew = new Brew();
		newBrew.setBrewName(brewName);
		newBrew.setBrewDate(new Date());
		newBrew.setBrewedBy(brc.user);
		brc.brewRepository.save(newBrew);

		String message = String.format("%s brewed a pot of %s.", brc.slackCommand.getMsgFriendlyUser(), brewName);

		return slackMessageFactory.createMessageBuilder().setText(message);
	}


	SlackMessageBuilder brewPrivate(BrewRequestContext brc)
	{
		logger.info("Brewing-private");

		return slackMessageFactory.createMessageBuilder().setText(String.format("You truly are a brew master %s.",brc.slackCommand.getMsgFriendlyUser()));
	}


	SlackMessageBuilder gone(BrewRequestContext brc)
	{
		logger.info("Gone");

		brc.brewRepository.findByGone(false).stream()
			.map(brew -> {
				brew.setGone(true);
				return brew;
			})
			.forEach(brc.brewRepository::save);

		String message = String.format("%s, go make some more coffee!", getBrewMasterRef());
		return slackMessageFactory.createMessageBuilder().setText(message);
	}


	SlackMessageBuilder gonePrivate(BrewRequestContext brc)
	{
		logger.info("Gone-private");

		return slackMessageFactory.createMessageBuilder().setText(String.format("The coffee is gone, this is unfortunate."));
	}


	SlackMessageBuilder debug(BrewRequestContext brc)
	{
		logger.info("Debugging");

		String response = brc.slackCommand.getAll().entrySet().stream()
			.map(entry -> entry.getKey() + " : " + entry.getValue())
			.reduce((a, b) -> a + "\n" + b)
			.get();

		return slackMessageFactory.createMessageBuilder().setText(response);
	}


	SlackMessageBuilder reset(BrewRequestContext brc)
	{
		logger.info("Reset");

		brc.brewRepository.deleteAll();

		return slackMessageFactory.createMessageBuilder().setText("It has been done.");
	}

	SlackMessageBuilder consume(BrewRequestContext brc)
	{
		return null;
	}


	String pretty(Brew brew) {
		String text = String.format("%s was brewed by %s at %s, and is %s.",
			brew.getBrewName(),
			brew.getBrewedBy(),
			fdf.format(brew.getBrewDate()),
			brew.isGone() ? "all gone" : "still available");

		return text;

	}

	public List<Brew> getTodaysBrews(BrewRepository brewRepository)
	{
		Instant twelveHoursAgo = Instant.now().minus(12, ChronoUnit.HOURS);

		return brewRepository
			.findTop20ByOrderByBrewDateDesc()
			.stream()
			.filter(brew -> brew.getBrewDate().toInstant().isAfter(twelveHoursAgo) )
			.collect(Collectors.toList());
	}

	private String getBrewMasterRef() {
		return new UserRef(brewMasterId,brewMasterUsername).getUserRef();
	}
}
