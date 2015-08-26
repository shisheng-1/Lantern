package org.lanternpowered.server.attribute.operation;

import org.lanternpowered.server.attribute.LanternOperation;

public class OperationMultiply extends LanternOperation {

    public OperationMultiply(String identifier, String name, int priority) {
        super(identifier, name, priority);
    }

    @Override
    public double getIncrementation(double base, double modifier, double currentValue) {
        return currentValue * modifier - currentValue;
    }

    @Override
    public boolean changeValueImmediately() {
        return false;
    }

}