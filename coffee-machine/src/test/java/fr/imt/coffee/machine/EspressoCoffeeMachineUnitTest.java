package fr.imt.coffee.machine;

import fr.imt.coffee.MainEspresso;
import fr.imt.coffee.machine.exception.CoffeeTypeCupDifferentOfCoffeeTypeTankException;
import fr.imt.coffee.machine.exception.LackOfWaterInTankException;
import fr.imt.coffee.machine.exception.MachineNotPluggedException;
import fr.imt.coffee.storage.cupboard.FabricCupboardContainer;
import fr.imt.coffee.storage.cupboard.coffee.type.CoffeeType;
import fr.imt.coffee.storage.cupboard.container.CoffeeContainer;
import fr.imt.coffee.storage.cupboard.container.CoffeeCup;
import fr.imt.coffee.storage.cupboard.container.Container;
import fr.imt.coffee.storage.cupboard.container.Cup;
import fr.imt.coffee.storage.cupboard.exception.CupNotEmptyException;
import io.cucumber.java.an.E;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

public class EspressoCoffeeMachineUnitTest {
    public EspressoCoffeeMachine espressoCoffeeMachineUnderTest;

    @BeforeEach
    public void beforeTest() {
        espressoCoffeeMachineUnderTest = new EspressoCoffeeMachine(0.20, 3, 0.20, 3, 600);
    }

    @AfterEach
    public void afterTest() {
        espressoCoffeeMachineUnderTest = null;
    }


    @Test
    public void testMachineNotPluggedException() {
        Cup mockCup = Mockito.mock(Cup.class);
        Mockito.when(mockCup.isEmpty()).thenReturn(true);

        Assertions.assertFalse(espressoCoffeeMachineUnderTest.isPlugged());

        Assertions.assertThrows(MachineNotPluggedException.class, ()->{
            espressoCoffeeMachineUnderTest.makeACoffee(mockCup, CoffeeType.MOKA);
        });
    }



    @Test
    public void testMakeACoffeeCupNotEmptyException(){
        Cup mockCup = Mockito.mock(Cup.class);
        Mockito.when(mockCup.isEmpty()).thenReturn(false);

        espressoCoffeeMachineUnderTest.plugToElectricalPlug();

        Assertions.assertThrows(CupNotEmptyException.class, ()->{
            espressoCoffeeMachineUnderTest.makeACoffee(mockCup, CoffeeType.MOKA);
        });
    }

    @Test
    public void testLackOfWaterInTankException(){
        Cup mockCup = Mockito.mock(Cup.class);
        Mockito.when(mockCup.isEmpty()).thenReturn(true);
        Mockito.when(mockCup.getCapacity()).thenReturn(0.25);

        espressoCoffeeMachineUnderTest.plugToElectricalPlug();

        espressoCoffeeMachineUnderTest.addWaterInTank(0.1);

        Assertions.assertThrows(LackOfWaterInTankException.class, ()->{
            espressoCoffeeMachineUnderTest.makeACoffee(mockCup, CoffeeType.MOKA);
        });
    }

    @Test
    public void testMakeACoffeeCoffeeTypeCupDifferentOfCoffeeTypeTankException(){
        Cup mockCup = Mockito.mock(Cup.class);
        Mockito.when(mockCup.isEmpty()).thenReturn(true);

        espressoCoffeeMachineUnderTest.plugToElectricalPlug();

        Assertions.assertThrows(CoffeeTypeCupDifferentOfCoffeeTypeTankException.class, ()->{
            espressoCoffeeMachineUnderTest.makeACoffee(mockCup, CoffeeType.MOKA);
        });
    }

    @Test
    public void testMakeCoffeeWithAllConditionsMet() throws Exception {
        Cup mockCup = Mockito.mock(Cup.class);
        Mockito.when(mockCup.isEmpty()).thenReturn(true);
        Mockito.when(mockCup.getCapacity()).thenReturn(0.25);
        Random randomMock = Mockito.mock(Random.class, Mockito.withSettings().withoutAnnotations());
        Mockito.when(randomMock.nextGaussian()).thenReturn(0.6);

        espressoCoffeeMachineUnderTest.setRandomGenerator(randomMock);

        espressoCoffeeMachineUnderTest.plugToElectricalPlug();
        Assertions.assertTrue(espressoCoffeeMachineUnderTest.isPlugged());

        espressoCoffeeMachineUnderTest.addWaterInTank(1.0);
        Assertions.assertTrue(espressoCoffeeMachineUnderTest.getWaterTank().getActualVolume() >= mockCup.getCapacity());

        espressoCoffeeMachineUnderTest.addCoffeeInBeanTank(0.5, CoffeeType.ARABICA_CREMA);
        Assertions.assertEquals(CoffeeType.ARABICA_CREMA, espressoCoffeeMachineUnderTest.getBeanTank().getBeanCoffeeType());

        CoffeeCup coffeeCup = (CoffeeCup) espressoCoffeeMachineUnderTest.makeACoffee(mockCup, CoffeeType.ARABICA_CREMA);

        Assertions.assertFalse(coffeeCup.isEmpty());
        Assertions.assertEquals(CoffeeType.ARABICA_CREMA, coffeeCup.getCoffeeType());
    }

    @Test
    public void testMainInstanceNotNull() {
        MainEspresso mainEspresso = new MainEspresso();
        Assertions.assertNotNull(mainEspresso);
    }




}

