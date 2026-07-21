package dev.lokspel.deathswap.commands;

public record RegisteredCommand(String name, SubCommand executor) {}
