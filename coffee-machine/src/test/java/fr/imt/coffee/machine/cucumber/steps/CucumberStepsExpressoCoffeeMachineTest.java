package fr.imt.coffee.machine.cucumber.steps;


import fr.imt.coffee.machine.EspressoCoffeeMachine;
import fr.imt.coffee.machine.exception.CoffeeTypeCupDifferentOfCoffeeTypeTankException;
import fr.imt.coffee.machine.exception.LackOfWaterInTankException;
import fr.imt.coffee.machine.exception.MachineNotPluggedException;
import fr.imt.coffee.storage.cupboard.coffee.type.CoffeeType;
import fr.imt.coffee.storage.cupboard.container.CoffeeContainer;
import fr.imt.coffee.storage.cupboard.container.Mug;
import fr.imt.coffee.storage.cupboard.exception.CupNotEmptyException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;

import java.util.Random;

public class CucumberStepsExpressoCoffeeMachineTest {
    public EspressoCoffeeMachine espressoCoffeeMachine;
    public Mug mug;
    public CoffeeContainer containerWithCoffee;

    @Given("an espresso coffee machine with {double} l of min capacity, {double} l of max capacity, {double} l per h of water flow for the pump")
    public void givenAnEspressoCoffeeMachine(double minimalWaterCapacity, double maximalWaterCapacity, double pumpWaterFlow){
        espressoCoffeeMachine = new EspressoCoffeeMachine(minimalWaterCapacity, maximalWaterCapacity, minimalWaterCapacity, maximalWaterCapacity, pumpWaterFlow);
    }

    @And("a mug with a capacity of {double}")
    public void aWithACapacityOf(double containerCapacity) {
        mug = new Mug(containerCapacity);
    }

    @When("I plug the espresso machine to electricity")
    public void iPlugTheMachineToElectricity() {
        espressoCoffeeMachine.plugToElectricalPlug();
    }

    @And("I add {double} liter of water in the water tank of the espresso machine")
    public void iAddLitersOfWater(double waterVolume) {
        espressoCoffeeMachine.addWaterInTank(waterVolume);
    }

    @And("I add {double} liter of {string} in the bean tank of the espresso machine")
    public void iAddLitersOfCoffeeBeans(double beanVolume, String coffeeType) {
        espressoCoffeeMachine.addCoffeeInBeanTank(beanVolume, CoffeeType.valueOf(coffeeType));
    }

    @And("I made a coffee {string} with the espresso machine")
    public void iMadeACoffee(String coffeeType) throws CoffeeTypeCupDifferentOfCoffeeTypeTankException, LackOfWaterInTankException, CupNotEmptyException, InterruptedException, MachineNotPluggedException {
        Random randomMock = Mockito.mock(Random.class, Mockito.withSettings().withoutAnnotations());
        Mockito.when(randomMock.nextGaussian()).thenReturn(0.6);
        espressoCoffeeMachine.setRandomGenerator(randomMock);

        containerWithCoffee = espressoCoffeeMachine.makeACoffee(mug, CoffeeType.valueOf(coffeeType));
        Assertions.assertNotNull(containerWithCoffee, "The coffee container should not be null after making a coffee.");
    }

    @Then("the espresso coffee machine return a coffee mug not empty")
    public void theEspressoCoffeeMachineReturnACoffeeMugNotEmpty() {
        Assertions.assertNotNull(containerWithCoffee, "The coffee container should not be null.");
        Assertions.assertFalse(containerWithCoffee.isEmpty(), "The coffee container should not be empty.");
    }

    @And("the espresso coffee machine return a coffee mug with a capacity of {double}")
    public void theEspressoCoffeeMachineReturnACoffeeMugWithACapacityOf(double capacity) {
        Assertions.assertNotNull(containerWithCoffee, "The coffee container should not be null.");
        Assertions.assertEquals(capacity, containerWithCoffee.getCapacity(), "The coffee container capacity should match the expected value.");
    }

    @And("a coffee mug containing a coffee type {string}")
    public void aCoffeeMugContainingACoffeeType(String coffeeType) {
        Assertions.assertNotNull(containerWithCoffee, "The coffee container should not be null.");
        Assertions.assertEquals(coffeeType, containerWithCoffee.getCoffeeType().toString(), "The coffee type should match the expected value.");
    }
}
