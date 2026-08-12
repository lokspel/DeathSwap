package dev.lokspel.deathswap.command;

public record RegisteredCommand(String name, SubCommand executor) {}
