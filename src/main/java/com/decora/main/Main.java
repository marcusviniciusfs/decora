package com.decora.main;

import com.decora.DecoraWS;
import com.decora.factory.DecoraWSFactory;
import com.decora.persistence.database.DatabaseService;
import com.decora.persistence.database.exception.DatabaseException;
import com.decora.persistence.database.migration.DatabaseMigrationService;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.ExecutionException;

enum CommandLineOption {

    HELP(Option.builder("h").longOpt("help").build()),

    MIGRATE_DB(Option.builder("m").longOpt("migrate-db").build()),

    MIGRATE_DB_DRY_RUN(Option.builder("d").longOpt("migrate-db-dry-run").build());

    private final Option o;

    CommandLineOption(final Option option) {
        o = option;
    }

    final boolean isInCMD(final CommandLine commandLine) {
        return commandLine.hasOption(o.getOpt());
    }

    final Option option() {
        return o;
    }
}

public final class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final String CMD_LINE_SYNTAX = "[option]";

    private Main() {
    }

    public static void main(final String[] args) {

        final DefaultParser defaultParser = new DefaultParser();

        final OptionGroup optionGroup = new OptionGroup()
                .addOption(CommandLineOption.HELP.option())
                .addOption(CommandLineOption.MIGRATE_DB.option())
                .addOption(CommandLineOption.MIGRATE_DB_DRY_RUN.option());

        optionGroup.setRequired(false);

        final Options options = new Options()
                .addOptionGroup(optionGroup);

        final CommandLine commandLine;
        try {
            commandLine = defaultParser.parse(options, args);
        } catch (ParseException e) {
            LOGGER.trace("", e);
            LOGGER.error("{}\n", e.getMessage());
            printHelp(options);
            return;
        }

        if (CommandLineOption.HELP.isInCMD(commandLine)) {
            printHelp(options);
        } else if (CommandLineOption.MIGRATE_DB.isInCMD(commandLine)) {
            new DatabaseMigrationService().execute(false);
        } else if (CommandLineOption.MIGRATE_DB_DRY_RUN.isInCMD(commandLine)) {
            new DatabaseMigrationService().execute(true);
        } else {
            start();
        }
    }

    private static void printHelp(final Options options) {
        final HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(CMD_LINE_SYNTAX, "Options:", options, null);
    }

    private static void start() {
        final DatabaseService databaseService;
        try {
            databaseService = new DatabaseService(DecoraWS.class.getSimpleName(), new File("hibernate.properties"));
        } catch (DatabaseException e) {
            LOGGER.trace("", e);
            LOGGER.error("", ExceptionUtils.getRootCause(e));
            return;
        }

        try {
            final DecoraWS decoraWS = DecoraWSFactory.create(databaseService);
            decoraWS.start().get();
        } catch (ExecutionException | InterruptedException e) {
            databaseService.close();
            LOGGER.trace("", e);
            LOGGER.error("", ExceptionUtils.getRootCause(e));
            Thread.currentThread().interrupt();
        }
    }
}
