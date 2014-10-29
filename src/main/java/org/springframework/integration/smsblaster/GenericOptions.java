package org.springframework.integration.smsblaster;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: kostas
 * Date: 4/19/12
 * Time: 10:35 AM
 * To change this template use File | Settings | File Templates.
 */
public class GenericOptions {

    @Option(name = "-e", aliases = {"--counterrors"}, usage = "count errors. Default true", required = false)
    public Boolean countErrors = true;

    @Option(name = "-n", aliases = {"--nthreads"}, usage = "number of blasters threads. Default is #urls", required = false)
    public Integer nthreads;

    @Argument(multiValued = true, usage = "arguments for the module", required = false)
    public List<String> args;

    @Option(name = "-h", aliases = {"--hits"}, usage = "total messages to send")
    public Long nhits;

    @Option(name = "-i", aliases = {"--interaction"}, usage = "interactive usage", required = false)
    public Boolean interactive = false;


}
