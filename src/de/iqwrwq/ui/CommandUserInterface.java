package de.iqwrwq.ui;

import com.bethecoder.ascii_table.ASCIITable;
import de.iqwrwq.client.Company;
import de.iqwrwq.core.Kernel;
import de.iqwrwq.server.ShipServer;
import de.iqwrwq.server.ShipThread;
import de.iqwrwq.server.objects.Cargo;
import de.iqwrwq.server.objects.Harbour;
import org.jetbrains.annotations.NotNull;
import org.tinylog.Logger;

import java.util.regex.Pattern;

public class CommandUserInterface extends UserInterface {

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
            case "cargos" -> listCargo(command);
            case "move" -> moveShip(command);
            case "load" -> loadShip(command);
            case "unload" -> shipServerRequired(command, "unload");
            case "ships" -> listAllShips();
            case "estate" -> printEstate();
            case "harbours" -> listHarbours();
            case "massmove" -> massMove();
            case "massunload" -> massUnload();
            default -> System.out.println("Error" + req.SEPARATOR + "UnhandledCommand");
        }
    }

    private void listCargo(@NotNull String command) {
        String[] tableHeaders = {"ID", "Source Harbour", "Destination Harbour", "Value"};
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

    private void moveShip(@NotNull String command) {
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

    private void loadShip(@NotNull String command) {
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

    private void shipServerRequired(@NotNull String command, String request) {
        int shipId = Integer.parseInt(command.split(Pattern.quote(" "))[1]);
        ShipThread shipThread = core.shipServer.shipConnectionMap.get(shipId);

        shipThread.communicationHandler.notifyServer(request);
    }

    private void listAllShips() {
        if (!core.shipServer.shipConnectionMap.isEmpty()) {
            String[] tableHeaders = {"Name", "ID", "Cargo", "Harbour"};
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
        String[] tableHeaders = {"Name", "Ships", "Cargos"};
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

    private void massMove() {
        if (core.config.massMove) {
            shipServer.shipConnectionMap.forEach((id, shipThread) -> {
                if (shipThread.cargo == null) {
                    for (Cargo cargo : shipServer.cargos) {
                        if(cargo.source.name.equals(shipThread.harbour.name)){
                            shipThread.cargo = cargo;
                            shipThread.communicationHandler.notifyServer("load");
                            shipThread.communicationHandler.notifyServer("move" + req.SEPARATOR + cargo.destination.name);
                            return;
                        }
                    }
                }else{
                    shipThread.communicationHandler.notifyServer("move" + req.SEPARATOR + shipThread.cargo.destination.name);
                }
            });
        } else {
            CommunicationHandler.forceMessage(ShipServer.INSTANCE_NAME, "massMoveDeactivated");
        }
    }

    private void massUnload(){
        if (core.config.massMove) {
            shipServer.shipConnectionMap.forEach((id, shipThread) -> {
                if (shipThread.cargo.destination.name.equals(shipThread.harbour.name)) {
                            shipThread.communicationHandler.notifyServer("unload");
                }else{
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
