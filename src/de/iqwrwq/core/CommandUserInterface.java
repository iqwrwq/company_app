package de.iqwrwq.core;

import java.util.Scanner;
import java.util.regex.Pattern;

public class CommandUserInterface extends Thread {

    private final Core core;
    private final ShipServer shipServer;
    private final Company company;

    public CommandUserInterface(Core core) {
        this.core = core;
        this.company = core.company;
        this.shipServer = core.shipServer;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        String userInput;
        do {
            userInput = scanner.nextLine();
            handleCommand(userInput);
        } while (!userInput.equalsIgnoreCase("exit"));
        endApplication();
    }

    private void handleCommand(String command) {
        switch (command.split(" ")[0]) {
            case "cargo" -> {
                if (command.split(Pattern.quote(" ")).length >= 2) {
                    core.shipServer.shipConnectionMap.get(Integer.parseInt(command.split(Pattern.quote(" "))[1])).communicationHandler.notifyServer("cargoinfo");
                } else {
                    System.out.println(core.company.companyName + req.SEPARATOR + "SeaTrade" + req.SEPARATOR + "getCargoInfo");
                    core.company.sendRequestToSeaTrade("getinfo:cargo");
                }
            }
            case "move" -> {
                if (command.split(Pattern.quote(" ")).length >= 2) {
                    core.shipServer.move(
                            Integer.parseInt(command.split(" ")[1]),
                            command.split(" -> ")[0].split(Pattern.quote(" "))[2]
                    );
                }
            }
            case "load" -> {
                if (command.split(Pattern.quote(" ")).length >= 2) {
                    core.shipServer.load(command);
                } else {
                    CommunicationHandler.forceMessage(
                            "ShipServer",
                            "Error" + req.SEPARATOR + "LoadWithoutShipId",
                            "\033[33m"
                    );
                }
            }
            case "unload" -> {
                core.shipServer.shipConnectionMap.get(Integer.parseInt(command.split(Pattern.quote(" "))[1])).communicationHandler.notifyServer("unload");
            }
            case "ships" -> {
                core.shipServer.shipConnectionMap.forEach((id, cargo) -> {
                    System.out.println(cargo.getInfo());
                });
            }
        }
    }

    private void endApplication() {
        company.exit();
        shipServer.exit();
    }
}
