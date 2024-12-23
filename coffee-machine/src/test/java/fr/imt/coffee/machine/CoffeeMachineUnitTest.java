package fr.imt.coffee.machine;

import fr.imt.coffee.MainCoffee;
import fr.imt.coffee.MainEspresso;
import fr.imt.coffee.machine.exception.CannotMakeCremaWithSimpleCoffeeMachine;
import fr.imt.coffee.machine.exception.CoffeeTypeCupDifferentOfCoffeeTypeTankException;
import fr.imt.coffee.machine.exception.LackOfWaterInTankException;
import fr.imt.coffee.machine.exception.MachineNotPluggedException;
import fr.imt.coffee.storage.cupboard.coffee.type.CoffeeType;
import fr.imt.coffee.storage.cupboard.container.CoffeeCup;
import fr.imt.coffee.storage.cupboard.container.Cup;
import fr.imt.coffee.storage.cupboard.exception.CupNotEmptyException;
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

public class CoffeeMachineUnitTest {
    public CoffeeMachine coffeeMachineUnderTest;

    /**
     * @BeforeEach est une annotation permettant d'exécuter la méthode annotée avant chaque test unitaire
     * Ici avant chaque test on initialise la machine à café
     */
    @BeforeEach
    public void beforeTest(){
        coffeeMachineUnderTest = new CoffeeMachine(
                0,10,
                0,10,  700);
    }

    /**
     * On vient tester si la machine ne se met pas en défaut
     */
    @Test
    public void testMachineFailureTrue(){
        //On créé un mock de l'objet random
        Random randomMock = Mockito.mock(Random.class, Mockito.withSettings().withoutAnnotations());
        //On vient ensuite stubber la méthode nextGaussian pour pouvoir contrôler la valeur retournée
        //ici on veut qu'elle retourne 1.0
        //when : permet de définir quand sur quelle méthode établir le stub
        //thenReturn : va permettre de contrôler la valeur retournée par le stub
        Mockito.when(randomMock.nextGaussian()).thenReturn(1.0);
        //On injecte ensuite le mock créé dans la machine à café
        coffeeMachineUnderTest.setRandomGenerator(randomMock);

        //On vérifie que le booleen outOfOrder est bien à faux avant d'appeler la méthode
        Assertions.assertFalse(coffeeMachineUnderTest.isOutOfOrder());
        //Ou avec Hamcrest
        assertThat(false, is(coffeeMachineUnderTest.isOutOfOrder()));

        //on appelle la méthode qui met la machine en défaut
        //On a mocké l'objet random donc la valeur retournée par nextGaussian() sera 1
        //La machine doit donc se mettre en défaut
        coffeeMachineUnderTest.coffeeMachineFailure();

        Assertions.assertTrue(coffeeMachineUnderTest.isOutOfOrder());
        assertThat(true, is(coffeeMachineUnderTest.isOutOfOrder()));
    }

    /**
     * On vient tester si la machine se met en défaut
     */
    @Test
    public void testMachineFailureFalse(){
        //On créé un mock de l'objet random
        Random randomMock = Mockito.mock(Random.class, Mockito.withSettings().withoutAnnotations());
        //On vient ensuite stubber la méthode nextGaussian pour pouvoir contrôler la valeur retournée
        //ici on veut qu'elle retourne 0.6
        //when : permet de définir quand sur quelle méthode établir le stub
        //thenReturn : va permettre de contrôler la valeur retournée par le stub
        Mockito.when(randomMock.nextGaussian()).thenReturn(0.6);
        //On injecte ensuite le mock créé dans la machine à café
        coffeeMachineUnderTest.setRandomGenerator(randomMock);

        //On vérifie que le booleen outOfOrder est bien à faux avant d'appeler la méthode
        Assertions.assertFalse(coffeeMachineUnderTest.isOutOfOrder());
        //Ou avec Hamcrest
        assertThat(false, is(coffeeMachineUnderTest.isOutOfOrder()));

        //on appelle la méthode qui met la machine en défaut
        //On a mocker l'objet random donc la valeur retournée par nextGaussian() sera 0.6
        //La machine doit donc NE PAS se mettre en défaut
        coffeeMachineUnderTest.coffeeMachineFailure();

        Assertions.assertFalse(coffeeMachineUnderTest.isOutOfOrder());
        //Ou avec Hamcrest
        assertThat(false, is(coffeeMachineUnderTest.isOutOfOrder()));
    }

    /**
     * On test que la machine se branche correctement au réseau électrique
     */
    @Test
    public void testPlugMachine(){
        Assertions.assertFalse(coffeeMachineUnderTest.isPlugged());

        coffeeMachineUnderTest.plugToElectricalPlug();

        Assertions.assertTrue(coffeeMachineUnderTest.isPlugged());
    }

    /**
     * On test qu'une exception est bien levée lorsque que le cup passé en paramètre retourne qu'il n'est pas vide
     * Tout comme le test sur la mise en défaut afin d'avoir un comportement isolé et indépendant de la machine
     * on vient ici mocker un objet Cup afin d'en maitriser complétement son comportement
     * On ne compte pas sur "le bon fonctionnement de la méthode"
     */
    @Test
    public void testMakeACoffeeCupNotEmptyException(){
        Cup mockCup = Mockito.mock(Cup.class);
        Mockito.when(mockCup.isEmpty()).thenReturn(false);

        coffeeMachineUnderTest.plugToElectricalPlug();

        //assertThrows( [Exception class expected], [lambda expression with the method that throws an exception], [exception message expected])
        //AssertThrows va permettre de venir tester la levée d'une exception, ici lorsque que le contenant passé en
        //paramètre n'est pas vide
        //On teste à la fois le type d'exception levée mais aussi le message de l'exception
        Assertions.assertThrows(CupNotEmptyException.class, ()->{
                coffeeMachineUnderTest.makeACoffee(mockCup, CoffeeType.MOKA);
            });
    }

    /**
     *  on vérifie que la machine ne peut pas faire un café si elle n'est pas branchée
     */
    @Test
    public void testMakeACoffeeMachineNotPluggedException(){
        Cup mockCup = Mockito.mock(Cup.class);
        Mockito.when(mockCup.isEmpty()).thenReturn(true);

        //On vérifie que la machine n'est pas branchée
        Assertions.assertFalse(coffeeMachineUnderTest.isPlugged());

        //On vérifie que la machine n'est pas branchée
        Assertions.assertThrows(MachineNotPluggedException.class, ()->{
            coffeeMachineUnderTest.makeACoffee(mockCup, CoffeeType.MOKA);
        });
    }


    /**
     * On vérifie que la machine ne peut pas faire un café si le type de café demandé est différent de celui dans le réservoir
     */
    @Test
    public void testMakeACoffeeCoffeeTypeCupDifferentOfCoffeeTypeTankException(){
        Cup mockCup = Mockito.mock(Cup.class);
        Mockito.when(mockCup.isEmpty()).thenReturn(true);

        coffeeMachineUnderTest.plugToElectricalPlug();

        Assertions.assertThrows(CoffeeTypeCupDifferentOfCoffeeTypeTankException.class, ()->{
            coffeeMachineUnderTest.makeACoffee(mockCup, CoffeeType.MOKA);
        });
    }



    /**
     * on vérifie que la machine ne peut pas faire un café si elle n'a pas assez d'eau dans le réservoir
     */
    @Test
    public void testLackOfWaterInTankException(){
        Cup mockCup = Mockito.mock(Cup.class);
        Mockito.when(mockCup.isEmpty()).thenReturn(true);
        Mockito.when(mockCup.getCapacity()).thenReturn(0.25);

        coffeeMachineUnderTest.plugToElectricalPlug();

        coffeeMachineUnderTest.addWaterInTank(0.1);

        Assertions.assertThrows(LackOfWaterInTankException.class, ()->{
            coffeeMachineUnderTest.makeACoffee(mockCup, CoffeeType.MOKA);
        });
    }

    @Test
    public void testCannotMakeCremaWithSimpleCoffeeMachine() {
        Cup mockCup1 = Mockito.mock(Cup.class);
        Mockito.when(mockCup1.isEmpty()).thenReturn(true);
        Mockito.when(mockCup1.getCapacity()).thenReturn(0.25);
        Random randomMock = Mockito.mock(Random.class, Mockito.withSettings().withoutAnnotations());
        Mockito.when(randomMock.nextGaussian()).thenReturn(0.6);

        coffeeMachineUnderTest.setRandomGenerator(randomMock);
        coffeeMachineUnderTest.plugToElectricalPlug();

        coffeeMachineUnderTest.addWaterInTank(1.0);

        // Add coffee beans to the tank
        coffeeMachineUnderTest.addCoffeeInBeanTank(0.5, CoffeeType.ARABICA_CREMA);

        // Attempt to make a coffee and expect the CannotMakeCremaWithSimpleCoffeeMachine exception
        Assertions.assertThrows(CannotMakeCremaWithSimpleCoffeeMachine.class, () -> {
            coffeeMachineUnderTest.makeACoffee(mockCup1, CoffeeType.ARABICA_CREMA);
        });
    }

    @Test
    public void testMainInstanceNotNull() {
        MainCoffee mainMachine = new MainCoffee();
        Assertions.assertNotNull(mainMachine);
    }





    @AfterEach
    public void afterTest(){
        coffeeMachineUnderTest = null;
    }


}
