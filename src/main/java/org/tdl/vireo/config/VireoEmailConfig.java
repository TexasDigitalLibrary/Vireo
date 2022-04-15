package org.tdl.vireo.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.tdl.vireo.config.constant.ConfigurationName;
import org.tdl.vireo.model.repo.ConfigurationRepo;

@Configuration
public class VireoEmailConfig {

    @Autowired
    private ConfigurationRepo configurationRepo;

    @Value("${app.email.host:''}")
    private String host;

    @Value("${app.email.from:''}")
    private String from;

    @Value("${app.email.replyTo:''}")
    private String replyTo;

    @Value("${app.email.username:''}")
    private String username;

    @Value("${app.email.password:''}")
    private String password;

    @Value("${app.email.port:25}")
    private int port;

    @Value("${app.email.protocol:smtp}")
    private String protocol;

    @Value("${app.email.channel:clear}")
    private String channel;

    @Value("${app.email.encoding:UTF8}")
    private String encoding;

    @PostConstruct
    public void postConstruct() {
        host = getConfigValue(ConfigurationName.APPLICATION_MAIL_HOST, host);
        from = getConfigValue(ConfigurationName.APPLICATION_MAIL_FROM, from);
        replyTo = getConfigValue(ConfigurationName.APPLICATION_MAIL_REPLYTO, replyTo);

        // some hardcoded defaults
        port = getConfigValue(ConfigurationName.APPLICATION_MAIL_PORT, port);
        protocol = getConfigValue(ConfigurationName.APPLICATION_MAIL_PROTOCOL, protocol);
        username = getConfigValue(ConfigurationName.APPLICATION_MAIL_USER, username);
        password = getConfigValue(ConfigurationName.APPLICATION_MAIL_PASSWORD, password);
        channel = getConfigValue(ConfigurationName.APPLICATION_MAIL_CHANNEL, channel);
        encoding = getConfigValue(ConfigurationName.APPLICATION_MAIL_ENCODING, encoding);
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    private String getConfigValue(String name, String defaultValue) {
        String value = configurationRepo.getValueByName(name);
        return (value != null) ? value : defaultValue;
    }

    private Integer getConfigValue(String name, Integer defaultValue) {
        Integer value = Integer.getInteger(configurationRepo.getValueByName(name));
        return (value != null) ? value : defaultValue;
    }

}
