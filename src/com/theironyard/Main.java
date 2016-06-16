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
                    HashMap map = new HashMap();
                    Session session = request.session();
                    String name = session.attribute("userName");
                    User user = users.get(name);

                    if (user == null) {
                        return new ModelAndView(map, "index.html");
                    }
                    else {
                        map.put("createUser", user.messages);
                        map.put("createPass", user.password);
                        return new ModelAndView(user, "message.html");
                    }
                }),
                new MustacheTemplateEngine()
        );

        Spark.post(
                "/create-user",
                 (request, response) -> {
                     String name = request.queryParams("createUser");
                     String password = request.queryParams("createPass");
                     User user = users.get(name);

                     if (user == null) {
                         user = new User(name, password);
                         users.put(name, user);
                     }

                      else if (!password.equals(user.password)) {
                         throw new Exception("Invalid Password");
                     }
                     Session session = request.session();
                     session.attribute("userName", name);
                     response.redirect("/");
                     return "";
                 }

        );

        Spark.post(
                "/create-message",
                ((request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("userName");
                    User user = users.get(name);
                    String message = request.queryParams("createMessage");
                    Message mess = new Message(message, user.messages.size());
                    user.messages.add(mess);
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
                    Session session = request.session();
                    String name = session.attribute("userName");
                    User user = users.get(name);
                    int index = Integer.valueOf(request.queryParams("messageIndex"));
                    user.messages.get(index).message = request.queryParams("editMessage");
                    response.redirect("/");
                    return "";
                })
        );
        Spark.post(
                "/delete",
                ((request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("userName");
                    User user = users.get(name);
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
}