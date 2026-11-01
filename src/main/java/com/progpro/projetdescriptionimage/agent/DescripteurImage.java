package com.progpro.projetdescriptionimage.agent;


public interface DescripteurImage {

    /**
     *
     * @param image
     * @param question
     * @return
     * @throws DescriptionImpossibleException
     *                                        
     */
    Description decrire(Image image, String question) throws DescriptionImpossibleException;
}
