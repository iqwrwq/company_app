package de.iqwrwq.ui;

import de.iqwrwq.client.Company;
import de.iqwrwq.core.Kernel;
import de.iqwrwq.server.ShipServer;
import de.iqwrwq.server.ShipThread;
import de.iqwrwq.server.objects.Harbour;
import de.iqwrwq.server.objects.Ship;

import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Pattern;

public class CommandUserInterface extends Thread {

    private final Kernel core;
    private final ShipServer shipServer;
    private final Company company;

    public CommandUserInterface(Kernel core) {
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
            case "cargo" -> listCargo(command);
            case "move" -> moveShip(command);
            case "load" -> loadShip(command);
            case "unload" -> shipServerRequired(command, "unload");
            case "ships" -> listAllShips();
            case "estate" -> printEstate();
            case "harbours" -> listHarbours();
        }
    }

    private void listCargo(String command) {
        String[] cargoCommand = command.split(Pattern.quote(" "));
        int specificCargoCommandLength = 2;

        if (cargoCommand.length >= specificCargoCommandLength) {
            shipServerRequired(command, "cargoinfo");
        } else {
            CommunicationHandler.forceMessage(core.company.companyName, "SeaTrade" + req.SEPARATOR + "getCargoInfo");
            core.company.communicationHandler.notifyServer("getinfo:cargo");
        }
    }

    private void moveShip(String command) {
        String[] moveCommand = command.split(Pattern.quote(" "));
        int moveCommandValidationLength = 2;

        if (moveCommand.length >= moveCommandValidationLength) {
            int shipIdIndex = 1;
            int destinationHarbourIndex = 2;

            core.shipServer.move(
                    Integer.parseInt(moveCommand[shipIdIndex]),
                    command.split(req.SEPARATOR)[0].split(Pattern.quote(" "))[destinationHarbourIndex]
            );
        }
    }

    private void loadShip(String command) {
        String[] loadCommand = command.split(Pattern.quote(" "));
        int loadCommandValidationLength = 2;

        if (loadCommand.length >= loadCommandValidationLength) {
            core.shipServer.load(command);
        } else {
            CommunicationHandler.forceMessage(
                    "ShipServer",
                    "Error" + req.SEPARATOR + "LoadWithoutShipId",
                    "\033[33m"
            );
        }
    }

    private void shipServerRequired(String command, String request) {
        int shipId = Integer.parseInt(command.split(Pattern.quote(" "))[1]);
        ShipThread shipThread = core.shipServer.shipConnectionMap.get(shipId);

        shipThread.communicationHandler.notifyServer(request);
    }

    private void listAllShips() {
        if (!core.shipServer.shipConnectionMap.isEmpty()) {
            core.shipServer.shipConnectionMap.forEach((id, shipThread) -> {
                CommunicationHandler.forceMessage(
                        ShipServer.INSTANCE_NAME, shipThread.getInfo());
            });
        } else {
            CommunicationHandler.forceMessage(
                    ShipServer.INSTANCE_NAME,
                    "Error" + req.SEPARATOR + "CurrentlyNoShips",
                    "\033[33m"
            );
        }
    }

    private void printEstate() {
        core.company.communicationHandler.notifyApp(String.valueOf(core.company.estate));
    }

    private void listHarbours() {
        for(Harbour harbour : core.shipServer.harbours){
            System.out.print(harbour.name + req.DIVIDER);
        }
    }

    private void endApplication() {
        company.exit();
        shipServer.exit();
    }
}
