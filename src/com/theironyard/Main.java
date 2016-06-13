package com.theironyard;

import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;
import java.util.HashMap;

public class Main {

    static HashMap<String, User> users = new HashMap<>();

    static User getUser(Session session) {
        String name = session.attribute("userName");
        return users.get(name);
    }

    public static void main(String[] args) {

        Spark.init();

        Spark.get(
                "/",
                ((request, response) -> {

                    User user = getUser(request.session());

                    if (user == null) {
                        return new ModelAndView(null, "index.html");
                    }
                    else {
                        return new ModelAndView(user, "messages.html");
                    }
                }),
                new MustacheTemplateEngine()
        );

        Spark.post(
                "/create-user",
                ((request, response) -> {
                    String name = request.queryParams("createUser");
                    String password = request.queryParams("createPass");
                    User user = users.get(name);
                    if (user == null) {
                        user = new User(name, password);
                        users.put(name, user);
                    }
                    Session session = request.session();
                    session.attribute("userName", name);

                    if (users.get(name).password.equals(password)) {
                        response.redirect("/");
                        return "";
                    }
                    else {
                        Spark.halt("403");
                        return "";
                    }
                })
        );

        Spark.post(
                "/create-message",
                ((request, response) -> {
                    User user = getUser(request.session());
                    Message message = new Message(request.queryParams(("createMessage")));
                    user.messages.add(message);
                    response.redirect("/");
                    return "";
                })
        );

        Spark.post(
                "/logout",
                ((request, response) -> {
                    Session session = request.session();
                    session.invalidate();
                    response.redirect("/");
                    return "";
                })
        );
    }
}