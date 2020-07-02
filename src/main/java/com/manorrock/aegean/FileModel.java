/*
 *  Copyright (c) 2002-2020, Manorrock.com. All Rights Reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *      1. Redistributions of source code must retain the above copyright
 *         notice, this list of conditions and the following disclaimer.
 *
 *      2. Redistributions in binary form must reproduce the above copyright
 *         notice, this list of conditions and the following disclaimer in the
 *         documentation and/or other materials provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *  ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 *  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 *  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *  POSSIBILITY OF SUCH DAMAGE.
 */
package com.manorrock.aegean;

/**
 * The model used for displaying files/directories.
 *
 * @author Manfred Riem (mriem@manorrock.com)
 */
public class FileModel {
    
    /**
     * Stores the directory flag.
     */
    private boolean directory;

    /**
     * Set the filename.
     */
    private String filename;

    /**
     * Get the filename.
     * 
     * @return the filename.
     */
    public String getFilename() {
        return filename;
    }
    
    /**
     * Is directory.
     * 
     * @return true if it is, false otherwise.
     */
    public boolean isDirectory() {
        return directory;
    }

    /**
     * Set the filename.
     * 
     * @param filename the filename. 
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * Set the directory flag.
     * 
     * @param directory the directory flag. 
     */
    public void setDirectory(boolean directory) {
        this.directory = directory;
    }
}
