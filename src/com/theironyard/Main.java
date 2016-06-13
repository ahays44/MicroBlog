package com.theironyard;

import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.HashMap;

public class Main {

    static HashMap<String, User> users = new HashMap<>();

    public static void main(String[] args) {

        Spark.init();
        Spark.get(
                "/",
                ((request, response) -> {

                    User user = getUserFromSession(request.session());

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

                    if (users.get(name).password.equals(password)){
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
                    User user = getUserFromSession(request.session());
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
        Spark.post(
                "/edit",
                ((request, response) -> {
                    User user = getUserFromSession(request.session());
                    int index = Integer.valueOf(request.queryParams("messageIndex"));
                    user.messages.get(index).message = request.queryParams("editMessage");
                    response.redirect("/");
                    return "";
                })
        );
        Spark.post(
                "/delete",
                ((request, response) -> {
                    User user = getUserFromSession(request.session());
                    String input = request.queryParams("deleteMessage");
                    if (!input.isEmpty()) {
                        int index = Integer.valueOf(input);
                        user.messages.remove(index - 1);
                    }
                    else {
                        user.messages.remove(0);
                    }
                    response.redirect("/");
                    return "";
                })
        );
    }
    static User getUserFromSession(Session session) {
        String name = session.attribute("userName");
        return users.get(name);
    }
}