package de.iqwrwq.auto;

import de.iqwrwq.client.Company;
import de.iqwrwq.core.Kernel;
import de.iqwrwq.server.ShipServer;
import de.iqwrwq.server.entities.Cargo;
import de.iqwrwq.ui.req;

public class AutoApplicationBot extends Thread {

    private final Kernel core;
    private final Company company;
    private final ShipServer shipServer;

    public AutoApplicationBot(Kernel core) {
        this.core = core;
        this.company = core.company;
        this.shipServer = core.shipServer;
    }

    @SuppressWarnings({"BusyWait", "InfiniteLoopStatement"})
    @Override
    public void run() {
        var clock = new Object() {
            int timeCache = 5000;
        };

        do {
            try {
                sleep(clock.timeCache);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            clock.timeCache = 5000;
            if (!shipServer.shipConnectionMap.isEmpty()) {
                shipServer.shipConnectionMap.forEach((id, ship) -> {
                    if (!ship.isMoving) {
                        if (ship.cargo != null) {
                            if (ship.cargo.destination.name.equals(ship.harbour.name)) {
                                ship.communicationHandler.notifyServer("unload");
                            } else {
                                ship.communicationHandler.notifyServer("move" + req.SEPARATOR + ship.cargo.destination.name);
                            }
                        } else {
                            for (Cargo cargo : shipServer.cargos) {
                                if (cargo.source.name.equals(ship.harbour.name)) {
                                    ship.communicationHandler.notifyServer("load");
                                    return;
                                }
                            }
                            if (!shipServer.cargos.isEmpty()) {
                                ship.communicationHandler.notifyServer("move" + req.SEPARATOR + shipServer.getHarbour().name);
                            } else {
                                clock.timeCache = 10000;
                                System.out.println(ship.id + req.SEPARATOR + "currentlyNoCargos" + req.DIVIDER + "sleep" + req.SEPARATOR + clock.timeCache);
                            }
                        }
                    }
                });
            }
        }while(true);
    }
}
