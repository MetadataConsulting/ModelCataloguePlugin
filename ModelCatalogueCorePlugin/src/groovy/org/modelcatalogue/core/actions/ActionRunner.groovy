package org.modelcatalogue.core.actions

public interface ActionRunner extends Runnable {

    /**
     * Returns the print writer where can the action record it's progress or errors.
     * @return the print writer where can the action record it's progress or errors
     */
    PrintWriter getOut()

    /**
     * Sets the print writer where can the action record it's progress or errors.
     * @param writer the print writer where can the action record it's progress or errors
     */
    void setOut(PrintWriter writer)

    /**
     * Validates if the input parameters meets the expectations.
     *
     * Returns empty map if the parameters are valid a map where the keys are the keys of the parameters and
     * the value is the error message otherwise.
     *
     * @param params parameters to be validated
     * @return empty map if the parameters are valid a map where the keys are the keys of the parameters and
     * the value is the error message otherwise
     */
    Map<String, String> validate(Map<String, String> params)

    /**
     * Initializes the runner with given parameters which should be previously validated by validate method.
     *
     * @param params the parameters for this runner
     */
    void initWith(Map<String, String> params)

    /**
     * Returns true if the run failed.
     * @return true if the run failed
     */
    boolean isFailed()

    /**
     * Returns the natural name of action represented by this ActionRunner.
     * @return the natural name of action represented by this ActionRunner
     */
    String getNaturalName()

    /**
     * Returns the admin (creation) usage description of action represented by this ActionRunner.
     *
     * The description is supposed to be fetched from the instance before the #initWith method is called.
     *
     * @return the admin (creation) usage description of action represented by this ActionRunner
     */
    String getDescription()

    /**
     * Returns the message to be displayed in the user's prompt. Usually as question which will be in the front end
     * followed by Yes/Cancel buttons.
     *
     * The message is supposed to be fetched from the instance after the #initWith method is called and therefore
     * it can used the set parameters inside the message body.
     *
     * @return the message to be displayed in the user's prompt
     */
     String getMessage()
}