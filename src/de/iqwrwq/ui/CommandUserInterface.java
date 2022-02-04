package de.iqwrwq.ui;

import com.bethecoder.ascii_table.ASCIITable;
import de.iqwrwq.client.Company;
import de.iqwrwq.core.Kernel;
import de.iqwrwq.server.ShipServer;
import de.iqwrwq.server.ShipThread;
import de.iqwrwq.server.objects.Cargo;
import org.jetbrains.annotations.NotNull;
import org.tinylog.Logger;

import java.util.regex.Pattern;

public class CommandUserInterface extends UserInterface {

    public static String INSTANCE_NAME = "CommandoInterface";

    private final @NotNull Kernel core;
    private final ShipServer shipServer;
    private final Company company;

    public CommandUserInterface(@NotNull Kernel core) {
        this.core = core;
        this.company = core.company;
        this.shipServer = core.shipServer;
    }

    protected void handleCommand(@NotNull String command) {
        switch (command.split(" ")[0]) {
            case "cargos" -> listCargo();
            case "move" -> moveShip(command);
            case "load" -> loadShip(command);
            case "unload" -> shipServerRequired(command, "unload");
            case "ships" -> listAllShips();
            case "estate" -> printEstate();
            case "harbours" -> listHarbours();
            case "sync" -> syncAllCargos();
            case "massmove" -> massMove();
            case "massunload" -> massUnload();
        }
    }

    private void listCargo() {
        if (core.shipServer.cargos.isEmpty()) {
            CommunicationHandler.forceMessage(ShipServer.INSTANCE_NAME, "Error" + req.SEPARATOR + "HasNoCargo");
        } else {
            String[] tableHeaders = {"ID", "Source Harbour", "Destination Harbour", "Value", String.valueOf(core.shipServer.cargos.size())};
            String[][] tableData = new String[core.shipServer.cargos.size()][];
            var Referral = new Object() {
                int counter = 0;
            };

            core.shipServer.cargos.forEach((cargo) -> {
                tableData[Referral.counter] = cargo.getInfo();
                Referral.counter++;
            });
            ASCIITable.getInstance().printTable(tableHeaders, tableData);
        }
    }

    private void moveShip(@NotNull String command) {
        String[] moveCommand = command.split(Pattern.quote(" "));
        int shipIdIndex = 1;

        try {
            if (!core.shipServer.shipConnectionMap.containsKey(Integer.parseInt(moveCommand[shipIdIndex]))) {
                CommunicationHandler.forceMessage(ShipServer.INSTANCE_NAME, "Error" + req.SEPARATOR + "InvalidShipId", "\033[33m");
            } else {
                int destinationHarbourIndex = 2;
                String[] to = command.split(req.SEPARATOR)[0].split(Pattern.quote(" "));


                core.shipServer.move(
                        Integer.parseInt(moveCommand[shipIdIndex]),
                        to.length == 4 ? to[2] + " " + to[3] : to[2]
                );
            }
        } catch (Exception e) {
            CommunicationHandler.forceMessage(ShipServer.INSTANCE_NAME, "Error" + req.SEPARATOR + "FatalError", "\033[33m");
        }


    }

    private void loadShip(@NotNull String command) {

        String[] loadCommand = command.split(Pattern.quote(" "));
        int loadCommandValidationLength = 2;

        if (loadCommand.length >= loadCommandValidationLength) {
            try {
                core.shipServer.load(command);
            } catch (Exception e) {
                CommunicationHandler.forceMessage(ShipServer.INSTANCE_NAME, "Error" + req.SEPARATOR + "FatalError", "\033[33m");
            }
        } else {
            CommunicationHandler.forceMessage(
                    "ShipServer",
                    "Error" + req.SEPARATOR + "LoadWithoutShipId",
                    "\033[33m"
            );
        }
    }

    private void shipServerRequired(@NotNull String command, String request) {
        try {
            int shipId = Integer.parseInt(command.split(Pattern.quote(" "))[1]);
            ShipThread shipThread = core.shipServer.shipConnectionMap.get(shipId);

            shipThread.communicationHandler.notifyServer(request);
        } catch (Exception exception) {
            CommunicationHandler.forceMessage(ShipServer.INSTANCE_NAME, "Error" + req.SEPARATOR + "FatalError", "\033[33m");
        }
    }

    private void listAllShips() {
        if (!core.shipServer.shipConnectionMap.isEmpty()) {
            String[] tableHeaders = {"Name", "ID", "Cargo", "Harbour", String.valueOf(core.shipServer.shipConnectionMap.size())};
            String[][] tableData = new String[core.shipServer.shipConnectionMap.size()][];
            var Referral = new Object() {
                int counter = 0;
            };

            core.shipServer.shipConnectionMap.forEach((id, shipThread) -> {
                tableData[Referral.counter] = shipThread.getInfo();
                Referral.counter++;
            });
            ASCIITable.getInstance().printTable(tableHeaders, tableData);

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
        String[] tableHeaders = {"Name", "Ships", "Cargos", String.valueOf(core.shipServer.harbours.size())};
        String[][] tableData = new String[core.shipServer.harbours.size()][];
        var Referral = new Object() {
            int counter = 0;
        };

        core.shipServer.harbours.forEach((harbour) -> {
            tableData[Referral.counter] = harbour.getInfo();
            Referral.counter++;
        });
        ASCIITable.getInstance().printTable(tableHeaders, tableData);
    }

    private void syncAllCargos() {
        company.communicationHandler.notifyApp("SyncStarted");
        company.communicationHandler.notifyServer("getinfo:cargo");
    }

    private void massMove() {
        if (core.config.massMove) {
            shipServer.shipConnectionMap.forEach((id, shipThread) -> {
                if (shipThread.cargo == null) {
                    for (Cargo cargo : shipServer.cargos) {
                        if (cargo.source.name.equals(shipThread.harbour.name)) {
                            shipThread.cargo = cargo;
                            shipThread.communicationHandler.notifyServer("load");
                            shipThread.communicationHandler.notifyServer("move" + req.SEPARATOR + cargo.destination.name);
                            return;
                        }
                    }
                } else {
                    shipThread.communicationHandler.notifyServer("move" + req.SEPARATOR + shipThread.cargo.destination.name);
                }
            });
        } else {
            CommunicationHandler.forceMessage(ShipServer.INSTANCE_NAME, "massMoveDeactivated");
        }
    }

    private void massUnload() {
        if (core.config.massMove) {
            shipServer.shipConnectionMap.forEach((id, shipThread) -> {
                if (shipThread.cargo.destination.name.equals(shipThread.harbour.name)) {
                    shipThread.communicationHandler.notifyServer("unload");
                } else {
                    shipThread.communicationHandler.notifyServer("move" + req.SEPARATOR + shipThread.cargo.destination);
                }
            });
        } else {
            CommunicationHandler.forceMessage(ShipServer.INSTANCE_NAME, "massMoveDeactivated");
        }
    }

    protected void endApplication() {
        company.exit();
        shipServer.exit();
        Logger.info("Interface exited");
    }
}
