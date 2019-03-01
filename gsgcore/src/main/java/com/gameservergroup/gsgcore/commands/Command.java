package com.gameservergroup.gsgcore.commands;

import com.gameservergroup.gsgcore.commands.handler.CommandHandler;
import com.gameservergroup.gsgcore.commands.handler.FunctionalCommandHandler;
import com.gameservergroup.gsgcore.commands.handler.ICommandHandler;
import com.gameservergroup.gsgcore.exceptions.CommandParseException;

import java.util.HashSet;
import java.util.function.Predicate;

public class Command extends AbstractionCommandPost {

    private final HashSet<Predicate<ICommandHandler<?>>> predicates;
    private final FunctionalCommandHandler handler;

    public Command(HashSet<Predicate<ICommandHandler<?>>> predicates, FunctionalCommandHandler handler) {
        this.predicates = predicates;
        this.handler = handler;
    }

    @Override
    public void call(CommandHandler<?> commandHandler) throws CommandParseException {
        for (Predicate<ICommandHandler<?>> predicate : predicates) {
            if (!predicate.test(commandHandler)) {
                return;
            }
        }

        handler.handle(commandHandler);
    }
}
