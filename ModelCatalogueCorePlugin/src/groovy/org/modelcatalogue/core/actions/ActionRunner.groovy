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

}