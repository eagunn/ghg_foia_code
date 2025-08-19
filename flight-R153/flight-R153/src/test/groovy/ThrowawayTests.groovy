import spock.lang.Specification

/**
 * Created by alabdullahwi on 9/14/2015.
 */
class ThrowawayTests extends Specification {




    def "it should change Integer class from call in method " () {

        given:
        Test test = new Test();

        when:
        Integer someInt = 0
        test.add(someInt)

        then:
        someInt == 2


    }

}


class Test {

    public void add(Integer arg) {
        arg++;
        arg++;
        print arg
    }


}
