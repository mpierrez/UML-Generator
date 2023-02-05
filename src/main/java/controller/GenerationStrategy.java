package controller;

import java.io.File;
import java.io.IOException;

public interface GenerationStrategy
{
    void generate(File file, String savePath) throws IOException, ClassNotFoundException;
    void write(File file, String savePath) throws ClassNotFoundException, IOException;
}
