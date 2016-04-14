var Thread = Java.type("java.lang.Thread");


Thread.sleep(30000);


var messageBuilder = slackMessageFactory.createMessageBuilder();

messageBuilder.setText('<!channel> :radio:(funky 80\'s pop starts playing, you\'re feeling the groove.)');
messageBuilder.setChannelId(slackCommand.getChannelId());
messageBuilder.setIconUrl('http://38.media.tumblr.com/avatar_369b115beb6e_128.png');
messageBuilder.setUsername('Anonymous Transmisson');

hookRequest.send(messageBuilder.build());

Thread.sleep(10000);


var lyrics = {

verse1 : [
':heart: We\'re no strangers to loooove :heart:',
'You know the rules and *so* do  :eye:',
':ring: A full commitment\'s what I\'m      thinking ooof',
'You wouldn\'t get this from any other guy :yoda:',
':eye: just wanna tell you how I\'m _feeahling_',
'Gotta make you     understand :books:'],

chorus : [
'Never gonna give you :point_up:',
'Never gonna let you :point_down:',
'Never gonna :runner: around and :icecream: you',
'Never gonna make you :crying_cat_face:',
':zipper_mouth_face: goodbye',
'Never gonna tell a :lie: and :goberserk: you'
]

}

var params = slackCommand.getText().split(" ");

var choice = lyrics[params[1]];

for (var i = 0; i < choice.length; i++) {
	
	messageBuilder.setText(choice[i]);
	messageBuilder.setChannelId(slackCommand.getChannelId());
	messageBuilder.setIconUrl('http://38.media.tumblr.com/avatar_369b115beb6e_128.png');
	messageBuilder.setUsername('Rick Astley');

	hookRequest.send(messageBuilder.build());

	Thread.sleep(3000);
}
