import spock.lang.Specification

class ManageArticlesSpec extends Specification {
    List<String> articles
    Deque<String> seen

    def "Articles List"() {
        given: 'I have articles titled Pizza, Breadsticks'
        articles = ["Pizza", "Breadsticks"]
        when: 'I go to the list of articles'
        viewArticles()
        then: 'I should see "Pizza"'
        seen.poll() == "Pizza"
        and: 'I should see "Breadsticks"'
        seen.poll() == "Breadsticks"
    }

    private void viewArticles() {
        seen = new LinkedList(articles.collect { "${it}" })
    }
}
